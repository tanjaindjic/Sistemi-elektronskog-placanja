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
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.dto.FinishedPaymentDTO;
import sep.tim18.banka.model.dto.PCCrequestDTO;
import sep.tim18.banka.model.dto.PaymentDTO;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.RequestDTO;
import sep.tim18.banka.repository.KarticaRepository;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.PaymentInfoRepository;
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

    @Value("${requestToPCC}")
    private String requestToPCC;

    @Value("${replyToKP}")
    private String replyToKP;

    @Value("${siteAddress}")
    private String siteAddress;

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private KarticaRepository karticaRepository;

    @Autowired
    private PaymentInfoRepository paymentInfoRepository;


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
        Klijent prodavac = klijentRepository.findByMerchantID(request.getMerchantID());

        Transakcija t = new Transakcija();
        t.setUplacuje(null);
        t.setPrima(prodavac);
        t.setPaymentURL(null);
        t.setAcquirerTimestamp(new DateTime());
        t.setStatus(Status.K);
        t.setRacunPrimaoca(prodavac.getKartice().get(0).getBrRacuna());//TODO skontati kako biramo na koju karticu tj. racun uplacujemo novac prodavcu jer u NC pamtimo samo njegov merchantID
        t.setRacunPosiljaoca(null);
        t.setIznos(request.getIznos());
        t.setSuccessURL(request.getSuccessURL());
        t.setFailedURL(request.getFailedURL());
        t.setErrorURL(request.getErrorURL());
        t.setMerchantOrderId(request.getMerchantOrderID());
        t.setMerchantTimestamp(request.getMerchantTimestamp());

        transakcijaRepository.save(t);

        return t;
    }


    @Override
    public PaymentInfo createPaymentDetails(RequestDTO request) {
        Transakcija t = createTransaction(request);
        PaymentInfo paymentInfo = new PaymentInfo(t, getToken());
        paymentInfoRepository.save(paymentInfo);
        return paymentInfo;
    }

    @Override
    public boolean isTokenExpired(String token) {
        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);

        if(paymentInfo==null)
            return true;

        Transakcija t = paymentInfo.getTransakcija();
        if(t==null)
            return true;

        if(t.getStatus()==Status.E)
            return true;

        if(t!=null){
            //TODO proveriti da li ovo radi kako treba
            if(t.getAcquirerTimestamp().plusMinutes(tokenDuration).isBeforeNow()){
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

        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);
        Transakcija t = paymentInfo.getTransakcija();
        Klijent kupac = klijentRepository.findByKartice_pan(paymentDTO.getPan());
        if(kupac==null)
            return sendToPCC(t, token,paymentDTO);

        //TODO proveriti filter
        List<Kartica> match = kupac.getKartice().stream()
                .filter(s -> paymentDTO.getPan().equals(s.getPan()))
                .collect(Collectors.toList());

        if(match.isEmpty())
            return paymentFailed(paymentInfo, t, token, paymentDTO);

        if(match.get(0).getRaspolozivaSredstva() - t.getIznos()<0)
            return paymentFailed(paymentInfo, t, token, paymentDTO);

        return finishPayment(paymentInfo, t, token, paymentDTO);
    }

    @Override
    public ResponseEntity<Map> sendToPCC(Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException {
        t.setStatus(Status.C);
        //TODO proveriti da li ostaviti ovako ili cekati da banka kupca odgovori pcc-u i prosledi pored ostalog i broj racuna kupca sa kojeg je skinut iznos
        t.setRacunPosiljaoca(paymentDTO.getPan());//za sad imamo samo br kartice a ne i racuna onog ko placa
        transakcijaRepository.save(t);

        PCCrequestDTO pcCrequestDTO = new PCCrequestDTO();
        pcCrequestDTO.setAcquirerOrderID(t.getAcquirerOrderID());
        pcCrequestDTO.setAcquirerTimestamp(t.getAcquirerTimestamp());
        pcCrequestDTO.setCvv(paymentDTO.getCvv());
        pcCrequestDTO.setGodina(paymentDTO.getGodina());
        pcCrequestDTO.setMesec(paymentDTO.getMesec());
        pcCrequestDTO.setIme(paymentDTO.getIme());
        pcCrequestDTO.setPrezime(paymentDTO.getPrezime());
        pcCrequestDTO.setPan(paymentDTO.getPan());
        pcCrequestDTO.setIznos(t.getIznos());
        pcCrequestDTO.setReturnURL(siteAddress + "pccReply");

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(pcCrequestDTO);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(jsonInString, headers);
        ResponseEntity<String> response = restTemplate.exchange(requestToPCC, HttpMethod.POST, entity, String.class);
        Map retVal = new HashMap();
        //TODO dodati paymentSent stranicu jer ne smemo ni dozvoliti pristup i reci da je uplata uspesna, ni reci da je odbijena
        //bolje reci da je kupovina evidentirana i da ce biti poslat mejl kada bude odobrena da moze da skine sa sajta sta je kupio
        retVal.put("location", siteAddress + "paymentSent");
        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map> paymentFailed(PaymentInfo paymentInfo, Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException {
        t.setStatus(Status.N);
        t.setRacunPosiljaoca(paymentDTO.getPan());//pokusano da se plati sa ove kartice
        transakcijaRepository.save(t);

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.N);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getAcquirerOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getAcquirerTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        finishedPaymentDTO.setRedirectURL(t.getFailedURL());

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(finishedPaymentDTO);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(jsonInString, headers);
        ResponseEntity<String> response = restTemplate.exchange(replyToKP, HttpMethod.POST, entity, String.class);

        Map retVal = new HashMap();
        retVal.put("location", "/failed");
        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map> finishPayment(PaymentInfo paymentInfo, Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException {
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
        transakcijaRepository.save(t);
        //ovde su i merchant i acquirer isti jer je ista banka
        //TODO proveriti da li treba praviti 2 transakcije pri placanju, tj da li treba i za kupca jedan red u tabeli da se doda

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.U);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getAcquirerOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getAcquirerTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        finishedPaymentDTO.setRedirectURL(t.getSuccessURL());

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(finishedPaymentDTO);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(jsonInString, headers);
        ResponseEntity<String> response = restTemplate.exchange(replyToKP, HttpMethod.POST, entity, String.class);

        Map retVal = new HashMap();
        retVal.put("location", "/success");
        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }



}
