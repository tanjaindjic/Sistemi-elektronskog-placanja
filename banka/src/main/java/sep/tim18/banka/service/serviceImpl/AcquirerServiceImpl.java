package sep.tim18.banka.service.serviceImpl;

import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sep.tim18.banka.model.Kartica;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.*;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.repository.KarticaRepository;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.PaymentInfoRepository;
import sep.tim18.banka.repository.TransakcijaRepository;
import sep.tim18.banka.service.AcquirerService;

import javax.servlet.http.HttpServletResponse;

@Service
public class AcquirerServiceImpl implements AcquirerService {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private static int tokenDuration;

    @Value("${tokenDuration}")
    public void setsss(int s) {
        tokenDuration = s;
    }


    private static String requestToPCC;

    @Value("${requestToPCC}")
    public void setss(String s) {
        requestToPCC = s;
    }

    private static String replyToKP;

    @Value("${replyToKP}")
    public void setBs(String s) {
        replyToKP = s;
    }

    private static String BNumber;

    @Value("${BNumber}")
    public void setBURL(String bank1No) {
        BNumber = bank1No;
    }

    private static String BAddress;

    @Value("${BAddress}")
    public void setBA(String bankAdress) {
        BAddress = bankAdress;
    }

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private KarticaRepository karticaRepository;

    @Autowired
    private PaymentInfoRepository paymentInfoRepository;


    @Override
    public boolean validate(KPRequestDTO request) {
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
    public Transakcija createTransaction(KPRequestDTO request) {
        Klijent prodavac = klijentRepository.findByMerchantID(request.getMerchantID());

        Transakcija t = new Transakcija();
        t.setUplacuje(null);
        t.setPrima(prodavac);
        t.setPaymentURL(null);
        t.setTimestamp(new Date());
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
    public PaymentInfo createPaymentDetails(KPRequestDTO request) {
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
        	Date d0 = t.getTimestamp();
        	Calendar cl = Calendar.getInstance();
        	System.out.println("trenutno vreme: "+cl.toString());
            cl.setTime(d0);
        	System.out.println("postavljeno vreme: "+cl.toString());
            cl.add(Calendar.MINUTE, 5);
        	System.out.println("postavljeno vreme + 5 minuta: "+cl.toString());
            
            
          //  if(t.getTimestamp().plusMinutes(tokenDuration).isBeforeNow()){
            if(cl.after(Calendar.getInstance())){    
            	t.setStatus(Status.E);
                transakcijaRepository.save(t);
                return true;
            }
            else return false;


        }else return true;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Map> tryPayment(String token, BuyerInfoDTO buyerInfoDTO, HttpServletResponse response) throws IOException {

        Map<String, String> map = new HashMap<>();
        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);
        if(paymentInfo == null) {
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        Transakcija t = paymentInfo.getTransakcija();
        Klijent kupac = klijentRepository.findByKartice_pan(buyerInfoDTO.getPan());

        if (isTokenExpired(token)) {
            paymentFailed(paymentInfo, t, token, buyerInfoDTO);
            map.put("Location", "/expired");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if (kupac == null) {
            sendToPCC(t, token, buyerInfoDTO, paymentInfo.getPaymentID(), response);
            map.put("Location", "/paymentSent");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        //TODO proveriti filter
        List<Kartica> match = kupac.getKartice().stream()
                .filter(s -> buyerInfoDTO.getPan().equals(s.getPan()))
                .collect(Collectors.toList());

        if (match.isEmpty()) {
            paymentFailed(paymentInfo, t, token, buyerInfoDTO);
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if (match.get(0).getRaspolozivaSredstva() - t.getIznos() < 0) {
            paymentFailed(paymentInfo, t, token, buyerInfoDTO);
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }else{
            finishPayment(paymentInfo, t, token, buyerInfoDTO);
            map.put("Location", "/success");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }


    }

    @Override
    public void sendToPCC(Transakcija t, String token, BuyerInfoDTO buyerInfoDTO, Long paymentID, HttpServletResponse resp) throws JsonProcessingException {
        t.setStatus(Status.C);
        t.setRacunPosiljaoca(buyerInfoDTO.getPan());
        transakcijaRepository.save(t);

        PCCRequestDTO pccRequestDTO = new PCCRequestDTO();
        pccRequestDTO.setAcquirerOrderID(t.getOrderID());
        pccRequestDTO.setAcquirerTimestamp(t.getTimestamp());
        pccRequestDTO.setCvv(buyerInfoDTO.getCvv());
        pccRequestDTO.setGodina(buyerInfoDTO.getGodina());
        pccRequestDTO.setMesec(buyerInfoDTO.getMesec());
        pccRequestDTO.setIme(buyerInfoDTO.getIme());
        pccRequestDTO.setPrezime(buyerInfoDTO.getPrezime());
        pccRequestDTO.setPan(buyerInfoDTO.getPan());
        pccRequestDTO.setIznos(t.getIznos());
        //pccRequestDTO.setReturnURL(siteAddress + "pccReply");
        pccRequestDTO.setRacunPrimaoca(t.getRacunPrimaoca());
        pccRequestDTO.setBrojBankeProdavca(BNumber);

        //saljem na pcc pa kad dobijem odgovor prosledim koncentratoru
        Mono<ClientResponse> clientResponse = exchange(pccRequestDTO, requestToPCC);

        clientResponse.subscribe((response)->{
            HttpStatus statusCode = response.statusCode();
            try {
                //saljem koncentratoru rezultat placanja
                KPReply(statusCode, paymentID, t, resp);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });


    }

    @Override
    public void paymentFailed(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException {
        t.setStatus(Status.N);
        t.setRacunPosiljaoca(buyerInfoDTO.getPan());//pokusano da se plati sa ove kartice
        transakcijaRepository.save(t);

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.N);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        finishedPaymentDTO.setRedirectURL(t.getFailedURL());

        exchange(finishedPaymentDTO, replyToKP);

    }

    @Override
    public void finishPayment(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException {
        Klijent kupac = klijentRepository.findByKartice_pan(buyerInfoDTO.getPan());
        Kartica zaPlacanje = null;
        for(Kartica k : kupac.getKartice())
            if(k.getPan().equals(buyerInfoDTO.getPan())){
                zaPlacanje = k;
                break;
            }

        int idx = kupac.getKartice().indexOf(zaPlacanje);
        Float raspolozivo = zaPlacanje.getRaspolozivaSredstva();
        zaPlacanje.setRaspolozivaSredstva(raspolozivo - t.getIznos());
        karticaRepository.save(zaPlacanje);
        kupac.getKartice().set(idx, zaPlacanje);
        klijentRepository.save(kupac);
        t.setStatus(Status.U);
        t.setRacunPrimaoca(kupac.getKartice().get(0).getBrRacuna());
        transakcijaRepository.save(t);
        //ovde su i merchant i acquirer isti jer je ista banka

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.U);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        finishedPaymentDTO.setRedirectURL(t.getSuccessURL());

        exchange(finishedPaymentDTO, replyToKP);

    }

    private void KPReply(HttpStatus responseCode, Long paymentID, Transakcija t, HttpServletResponse response) throws IOException {
        KPReplyDTO kpReplyDTO = new KPReplyDTO();
        kpReplyDTO.setAcquirerOrderID(t.getOrderID());
        kpReplyDTO.setAcquirerTimestamp(t.getTimestamp());
        kpReplyDTO.setMerchantOrderID(t.getMerchantOrderId());
        kpReplyDTO.setPaymentID(paymentID);

        if(responseCode==HttpStatus.BAD_REQUEST) {
            kpReplyDTO.setStatus(Status.N);
            response.sendRedirect("/failed");
        }
        else if(responseCode==HttpStatus.OK) {
            kpReplyDTO.setStatus(Status.U);
            response.sendRedirect("/success");
        }
        exchange(kpReplyDTO, replyToKP);

    }

    private Mono<ClientResponse> exchange(Object object, String uri) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(object);

        WebClient client = WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        WebClient.RequestHeadersSpec<?> requestSpec = WebClient
                .create()
                .post()
                .uri(URI.create(uri))
                .body(BodyInserters.fromObject(jsonInString));

        Mono<ClientResponse> clientResponse = requestSpec
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                .exchange();
        return clientResponse;
    }


}
