package sep.tim18.banka.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sep.tim18.banka.model.Kartica;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.dto.FinishedPaymentDTO;
import sep.tim18.banka.model.dto.PCCrequestDTO;
import sep.tim18.banka.model.dto.PaymentDTO;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.RequestDTO;
import sep.tim18.banka.repository.KarticaRepository;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.TransakcijaRepository;
import sep.tim18.banka.service.MainService;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MainServiceImpl implements MainService {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Value("${tokenDuration}")
    private int tokenDuration;

    @Value("${pccURL}")
    private String pccURL;

    @Value("${siteAddress}")
    private String siteAddress;

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private KarticaRepository karticaRepository;


    @Override
    public boolean validate(RequestDTO request) {
        Klijent prodavac = klijentRepository.findByMerchantID(request.getMerchantID());
        if(prodavac==null)
            return false;
        //TODO videti kako cuvati pass pa po tome i proveravati
        if(!prodavac.getMerchantPass().equals(request.getMerchantPass()))
            return false;
        return true;
    }

    @Override
    public String getToken() {
        StringBuilder sb = new StringBuilder( 50 );
        for( int i = 0; i < 50; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    @Override
    public Transakcija createTransaction(RequestDTO request) {
        //kreiram novu transakciju
        Transakcija t = new Transakcija();
        Klijent prodavac = klijentRepository.findByMerchantID(request.getMerchantID());
        //TODO skontati kako biramo na koju karticu tj. racun uplacujemo novac prodavcu jer u NC pamtimo samo njegov merchantID
        t.setPrima(prodavac);
        t.setRacunPrimaoca(prodavac.getKartice().get(0).getBrRacuna());
        t.setIznos(request.getIznos());
        t.setStatus(Status.K);
        t.setVremeKreiranja(new DateTime());
        t.setPaymentURL(getToken());
        t.setSuccessURL(request.getSuccessURL());
        t.setFailedURL(request.getFailedURL());
        t.setErrorURL(request.getErrorURL());
        t.setMerchantOrderId(request.getMerchantOrderID());
        t.setMerchantTimestamp(request.getMerchantTimestamp());
        t.setRacunPosiljaoca(null);
        t.setVremeIzvrsenja(null);
        transakcijaRepository.save(t);

        return t;
    }

    @Override
    public boolean isTokenExpired(String token) {
        Transakcija t = transakcijaRepository.findByPaymentURL(token);
        if(t==null)
            return true;

        if(t.getStatus()==Status.E)
            return true;

        if(t!=null){

            //TODO proveriti da li ovo radi kako treba
            if(t.getVremeKreiranja().plusMinutes(tokenDuration).isBeforeNow()){
                t.setStatus(Status.E);
                transakcijaRepository.save(t);
                return true;
            }
            else return false;


        }else return true;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Map> tryPayment(String token, PaymentDTO paymentDTO) throws JsonProcessingException {
        Transakcija t = transakcijaRepository.findByPaymentURL(token);
        Klijent kupac = klijentRepository.findByKartice_pan(paymentDTO.getPan());
        if(kupac==null)
            return sendToPCC(t, token,paymentDTO);

        //TODO proveriti filter
        List<Kartica> match = kupac.getKartice().stream()
                .filter(s -> paymentDTO.getPan().equals(s.getPan()))
                .collect(Collectors.toList());
        if(match.get(0).getRaspolozivaSredstva()-t.getIznos()<0)
            return paymentFailed(t, token, paymentDTO);

        return finishPayment(t, token, paymentDTO);
    }

    @Override
    public ResponseEntity<Map> sendToPCC(Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException {
        t.setStatus(Status.C);
        //TODO proveriti da li ostaviti ovako ili cekati da banka kupca odgovori pcc-u i prosledi pored ostalog i broj racuna kupca sa kojeg je skinut iznos
        t.setRacunPosiljaoca(paymentDTO.getPan());//za sad imamo samo br kartice a ne i racuna onog ko placa
        transakcijaRepository.save(t);

        //prema specifikaciji ako se salje na pcc zahtev pravi se nova transakcija, evidentira se komunikacija sa njim i bankom kupca
        Transakcija zaSlanje = new Transakcija(t);

        PCCrequestDTO pcCrequestDTO = new PCCrequestDTO();
        pcCrequestDTO.setAcquirerOrderID(zaSlanje.getId());
        pcCrequestDTO.setAcquirerTimestamp(zaSlanje.getVremeKreiranja());
        pcCrequestDTO.setCvv(paymentDTO.getCvv());
        pcCrequestDTO.setGodina(paymentDTO.getGodina());
        pcCrequestDTO.setMesec(paymentDTO.getMesec());
        pcCrequestDTO.setIme(paymentDTO.getIme());
        pcCrequestDTO.setPrezime(paymentDTO.getPrezime());
        pcCrequestDTO.setPan(paymentDTO.getPan());

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(pcCrequestDTO);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(jsonInString, headers);
        ResponseEntity<String> response = restTemplate.exchange(pccURL, HttpMethod.POST, entity, String.class);
        Map retVal = new HashMap();
        //TODO dodati paymentSent stranicu jer ne smemo ni dozvoliti pristup i reci da je uplata uspesna, ni reci da je odbijena
        //bolje reci da je kupovina evidentirana i da ce biti poslat mejl kada bude odobrena da moze da skine sa sajta sta je kupio
        retVal.put("Location", siteAddress + "paymentSent");
        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map> paymentFailed(Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException {
        t.setStatus(Status.N);
        t.setVremeIzvrsenja(new DateTime());
        t.setRacunPosiljaoca(paymentDTO.getPan());
        transakcijaRepository.save(t);

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.N);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getId()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getVremeIzvrsenja());
        finishedPaymentDTO.setPaymentID(t.getId());
        finishedPaymentDTO.setRedirectURL(t.getFailedURL());

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(finishedPaymentDTO);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(jsonInString, headers);
        ResponseEntity<String> response = restTemplate.exchange(pccURL, HttpMethod.POST, entity, String.class);

        Map retVal = new HashMap();
        retVal.put("location", "/expired");
        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map> finishPayment(Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException {
        Klijent kupac = klijentRepository.findByKartice_pan(paymentDTO.getPan());
        Kartica zaPlacanje = null;
        for(Kartica k : kupac.getKartice())
            if(k.getPan().equals(paymentDTO.getPan())){
                zaPlacanje = k;
                break;
            }
        Float raspolozivo = zaPlacanje.getRaspolozivaSredstva();
        zaPlacanje.setRaspolozivaSredstva(raspolozivo - t.getIznos());
        karticaRepository.save(zaPlacanje);
        int idx = kupac.getKartice().indexOf(zaPlacanje);
        kupac.getKartice().set(idx, zaPlacanje);
        klijentRepository.save(kupac);
        t.setStatus(Status.U);
        t.setRacunPrimaoca(kupac.getKartice().get(0).getBrRacuna());
        t.setVremeIzvrsenja(new DateTime());
        transakcijaRepository.save(t);
        //ovde su i merchant i acquirer isti jer je ista banka
        //TODO proveriti da li treba praviti 2 transakcije pri placanju, tj da li treba i za kupca jedan red u tabeli da se doda

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.U);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getId()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getVremeIzvrsenja());
        finishedPaymentDTO.setPaymentID(t.getId());
        finishedPaymentDTO.setRedirectURL(t.getSuccessURL());

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(finishedPaymentDTO);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(jsonInString, headers);
        ResponseEntity<String> response = restTemplate.exchange(pccURL, HttpMethod.POST, entity, String.class);

        Map retVal = new HashMap();
        retVal.put("location", "/paymentPassed");
        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }


}
