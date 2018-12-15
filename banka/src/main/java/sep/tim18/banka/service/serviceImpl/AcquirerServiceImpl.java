package sep.tim18.banka.service.serviceImpl;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.web.client.RestTemplate;
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

import javax.net.ssl.HttpsURLConnection;
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
        t.setPanPrimaoca(prodavac.getKartice().get(0).getPan());
        t.setPanPosaljioca(null);
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
        t.setPaymentURL(paymentInfo.getPaymentURL());
        transakcijaRepository.save(t);
        paymentInfoRepository.save(paymentInfo);
        return paymentInfo;
    }

    @Override
    public boolean finishedPayment(String token) {
        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);
        Transakcija t = paymentInfo.getTransakcija();
        if(t.getStatus()==Status.U || t.getStatus()==Status.N)
            return true;
        else return false;
    }


    @Override
    public boolean checkCredentials(String token, BuyerInfoDTO buyerInfoDTO) {
        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);
        if(paymentInfo==null)
            return false;
        Transakcija t = paymentInfo.getTransakcija();
        if(t==null)
            return false;

        Klijent kupac = klijentRepository.findByKartice_pan(buyerInfoDTO.getPan());
        if(kupac==null) //onda nije iz nase banke pa issuer treba ovo da proveri
            return true;
        if(!kupac.getIme().toLowerCase().equals(buyerInfoDTO.getIme().toLowerCase().trim()))
            return false;
        if(!kupac.getPrezime().toLowerCase().equals(buyerInfoDTO.getPrezime().toLowerCase().trim()))
            return false;
        if(!kupac.getKartice().get(0).getCcv().equals(buyerInfoDTO.getCvv().trim()))
            return false;
        String expDate = buyerInfoDTO.getMesec() + "/" + buyerInfoDTO.getGodina();
        if(!kupac.getKartice().get(0).getExpDate().equals(expDate))
            return false;

        return true;
    }

    @Override
    public boolean isTokenExpired(String token) {
        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);

        if(paymentInfo==null)
            return true;

        Transakcija t = paymentInfo.getTransakcija();
        if(t==null)
            return true;

        if(t.getStatus()==Status.E || t.getStatus()==Status.U || t.getStatus()==Status.N)
            return true;

        if(t!=null){
            Calendar postavljeno = Calendar.getInstance();
            postavljeno.setTime(t.getTimestamp());
            Calendar max = Calendar.getInstance();
            max.setTime(t.getTimestamp());
            max.add(Calendar.MINUTE, tokenDuration);

        	System.out.println("postavljeno vreme: "+postavljeno.getTime().toString());
            System.out.println("max vreme: "+max.getTime().toString());

            if(!max.after(postavljeno)){
                System.out.println("postavljoeno vreme nije nakon trenutnog -> isteklo");
            	t.setStatus(Status.E);
                transakcijaRepository.save(t);
                return true;
            }
            else return false;


        }else return true;
    }

    @Override//propagation never jer inace traje transakcija i nakon komunikacije sa pcc-om i dobije se lock table timeout
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.NEVER, isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Map> tryPayment(String token, BuyerInfoDTO buyerInfoDTO, HttpServletResponse response) throws IOException {

        Map<String, String> map = new HashMap<>();
        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);
        if(paymentInfo == null) {
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        Transakcija t = paymentInfo.getTransakcija();
        Klijent kupac = klijentRepository.findByKartice_pan(buyerInfoDTO.getPan());
        t.setPanPosaljioca(buyerInfoDTO.getPan());//pokusano da se plati sa ove kartice

        if(finishedPayment(token)){
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        if (isTokenExpired(token)) {
            t.setStatus(Status.N);
            transakcijaRepository.save(t);

            paymentFailed(paymentInfo, t, token, buyerInfoDTO);
            map.put("Location", "/expired");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if (kupac == null) {
            t.setStatus(Status.C);
            transakcijaRepository.save(t);

            sendToPCC(t, token, buyerInfoDTO, paymentInfo, response);
            map.put("Location", "/paymentSent");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        //TODO proveriti filter
        List<Kartica> match = kupac.getKartice().stream()
                .filter(s -> buyerInfoDTO.getPan().equals(s.getPan()))
                .collect(Collectors.toList());

        if (match.isEmpty()) {
            t.setStatus(Status.N);
            transakcijaRepository.save(t);

            paymentFailed(paymentInfo, t, token, buyerInfoDTO);
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if (match.get(0).getRaspolozivaSredstva() - t.getIznos() < 0) {
            t.setStatus(Status.N);
            transakcijaRepository.save(t);

            paymentFailed(paymentInfo, t, token, buyerInfoDTO);
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }else{
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

            Kartica primalac = karticaRepository.findByPan(t.getPanPrimaoca());
            raspolozivo = primalac.getRaspolozivaSredstva();
            primalac.setRaspolozivaSredstva(raspolozivo + t.getIznos());
            karticaRepository.save(primalac);
            Klijent prodavac = klijentRepository.findByKartice_pan(primalac.getPan());
            idx = prodavac.getKartice().indexOf(primalac);
            prodavac.getKartice().set(idx, primalac);
            klijentRepository.save(prodavac);

            t.setStatus(Status.U);
            t.setPanPrimaoca(kupac.getKartice().get(0).getBrRacuna());
            transakcijaRepository.save(t);

            finishPayment(paymentInfo, t, token, buyerInfoDTO);
            map.put("Location", "/success");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }


    }


    @Override
    public void sendToPCC(Transakcija t, String token, BuyerInfoDTO buyerInfoDTO, PaymentInfo paymentInfo, HttpServletResponse resp) throws JsonProcessingException {
        PCCRequestDTO pccRequestDTO = new PCCRequestDTO();
        pccRequestDTO.setAcquirerOrderID(t.getOrderID());
        pccRequestDTO.setAcquirerTimestamp(t.getTimestamp());
        pccRequestDTO.setCvv(buyerInfoDTO.getCvv());
        pccRequestDTO.setGodina(buyerInfoDTO.getGodina());
        pccRequestDTO.setMesec(buyerInfoDTO.getMesec());
        pccRequestDTO.setIme(buyerInfoDTO.getIme());
        pccRequestDTO.setPrezime(buyerInfoDTO.getPrezime());
        pccRequestDTO.setPanPosaljioca(buyerInfoDTO.getPan());
        pccRequestDTO.setIznos(t.getIznos());
        pccRequestDTO.setReturnURL(BAddress + "pccReply");
        pccRequestDTO.setPanPrimaoca(t.getPanPrimaoca());
        pccRequestDTO.setBrojBankeProdavca(BNumber);
        pccRequestDTO.setMerchantOrderID(t.getMerchantOrderId());
        pccRequestDTO.setMerchantTimestamp(t.getMerchantTimestamp());

        //saljem na pcc pa kad dobijem odgovor prosledim koncentratoru
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try{
            template.postForEntity(requestToPCC, pccRequestDTO, PCCRequestDTO.class);
        }catch(Exception e){
            System.out.println("greska kod slanja zahteva na pcc");

        }

    }


    @Override
    public void paymentFailed(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException {
        System.out.println("Usao u paymentFailed");

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.N);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        finishedPaymentDTO.setRedirectURL(t.getFailedURL());

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try{
            template.postForEntity(replyToKP, finishedPaymentDTO, FinishedPaymentDTO.class);
        }catch(Exception e){
            System.out.println("KP nije dostupan");

        }

    }

    @Override
    public void finishPayment(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException {

        //ovde su i merchant i acquirer isti jer je ista banka

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.U);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        finishedPaymentDTO.setRedirectURL(t.getSuccessURL());

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(replyToKP, finishedPaymentDTO, FinishedPaymentDTO.class);
        } catch(Exception e) {
            System.out.println("KP nije dostupan");

        }

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.NEVER, isolation = Isolation.SERIALIZABLE)
    public void finalizePayment(PCCReplyDTO pccReplyDTO) {
        Transakcija t = transakcijaRepository.findById(pccReplyDTO.getAcquirerOrderID()).get();
        PaymentInfo paymentInfo = paymentInfoRepository.findByTransakcija(t);
        if(pccReplyDTO.getStatus()==Status.N)
            t.setStatus(Status.N);
        else{
            t.setStatus(Status.U);
            Kartica primalac = karticaRepository.findByPan(t.getPanPrimaoca());
            Float raspolozivo = primalac.getRaspolozivaSredstva();
            primalac.setRaspolozivaSredstva(raspolozivo + t.getIznos());
            karticaRepository.save(primalac);
            Klijent prodavac = klijentRepository.findByKartice_pan(primalac.getPan());
            int idx = prodavac.getKartice().indexOf(primalac);
            prodavac.getKartice().set(idx, primalac);
            klijentRepository.save(prodavac);

        }
        transakcijaRepository.save(t);
        sendReplyToKP(t, paymentInfo);
    }

    private void sendReplyToKP(Transakcija t, PaymentInfo paymentInfo){

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(t.getStatus());
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        if(t.getStatus()==Status.N)
            finishedPaymentDTO.setRedirectURL(t.getFailedURL());
        else finishedPaymentDTO.setRedirectURL(t.getSuccessURL());

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(replyToKP, finishedPaymentDTO, FinishedPaymentDTO.class);
        }catch(Exception e){
            System.out.println("KP nije dostupan");

        }

    }

   /* private Klijent getByPanKartice(String pan){
        Kartica k = karticaRepository.findByPan(pan);
        for(Klijent kl : klijentRepository.findAll())
            for(Kartica ka : kl.getKartice())
                if(ka==k)
                    return kl;

        return null;
    }*/



}
