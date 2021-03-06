package sep.tim18.banka.service.serviceImpl;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import sep.tim18.banka.exceptions.FundsException;
import sep.tim18.banka.exceptions.NotFoundException;
import sep.tim18.banka.exceptions.PaymentException;
import sep.tim18.banka.model.*;
import sep.tim18.banka.model.dto.*;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.repository.*;
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

    private static String pccSync;

    @Value("${pccSync}")
    public void setpccsync(String s) {
        pccSync = s;
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

    private static boolean taskEnabled;

    @Value("${TASK_ENABLED}")
    public void setEnabled(Boolean enabled) {
        taskEnabled = enabled;
    }

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private KarticaRepository karticaRepository;

    @Autowired
    private PaymentInfoRepository paymentInfoRepository;

    @Autowired
    private PCCRequestRepository pccRequestRepository;

    @Scheduled(initialDelay = 5000, fixedRate = 30000)
    public void getTransactions() {
        if(!taskEnabled){//u slucaju issuera ne bi trebalo da se pokrecu ovi taskovi
            System.out.println("SCHEDULED TASK---not enabled on ISSUER server");
            return;
        }
        System.out.println("SCHEDULED TASK---Kontaktira PCC da preuzme neresene transakcije");
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<List<PCCReplyDTO>> response = restTemplate.exchange(pccSync,HttpMethod.GET,null,new ParameterizedTypeReference<List<PCCReplyDTO>>() {});
            List<PCCReplyDTO> preostaleTransakcije = response.getBody();
            System.out.println("SCHEDULED TASK---Primio sa PCC-a: " + preostaleTransakcije.size());
            for (PCCReplyDTO pccReplyDTO : preostaleTransakcije) {
                Transakcija t = transakcijaRepository.findById(pccReplyDTO.getAcquirerOrderID()).get();
                save(t,pccReplyDTO.getStatus() );
            }
        }catch (Exception e){
            System.out.println("SCHEDULED TASK---PCC nije dostupan.");
        }
    }

    @Scheduled(initialDelay = 5000, fixedRate = 30000)
    public void sendRemainingTransactions() throws JsonProcessingException {
        if(!taskEnabled){
            System.out.println("SCHEDULED TASK---not enabled on ISSUER server");
            return;
        }
        System.out.println("SCHEDULED TASK---Usao u slanje neposlatih transakcija na PCC");
        if(transakcijaRepository.findByStatus(Status.C_PCC).isEmpty()){
            System.out.println("SCHEDULED TASK---Nema neposlatih transakcija.");
            return;
        }
        RestTemplate template = new RestTemplate();
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        for(Transakcija t : transakcijaRepository.findByStatus(Status.C_PCC)) {

            PCCRequest pccRequest = pccRequestRepository.findByMerchantOrderID(t.getMerchantOrderId());
            PCCRequestDTO pccRequestDTO = new PCCRequestDTO(pccRequest);

            try {
                save(t, Status.C);
                template.postForEntity(requestToPCC, pccRequestDTO, PCCRequestDTO.class);
                pccRequestRepository.delete(pccRequest);
                System.out.println("SCHEDULED TASK---Poslata transakcija ID: " + t.getOrderID());
            } catch (Exception e) {
                System.out.println("SCHEDULED TASK---PCC nije dostupan. Transakcija ID: " + t.getOrderID() + " nije poslata.");
                save(t,Status.C_PCC );
            }
        }

    }

    @Scheduled(initialDelay = 5000, fixedRate = 30000)
    public void checkExpiredTransactions() {
        System.out.println("SCHEDULED TASK---provera vazenja tokena");
        for(Transakcija t : transakcijaRepository.findByStatus(Status.K)){
            if(isTokenExpired(t.getPaymentURL())){
                System.out.println("Transakcija ID: " + t.getOrderID() + " je istekla.");
                save(t, Status.E_KP);

            }
        }
        for(Transakcija t : transakcijaRepository.findByStatus(Status.K_KP)){
            if(isTokenExpired(t.getPaymentURL())){
                System.out.println("Transakcija ID: " + t.getOrderID() + " je istekla.");
                save(t, Status.E_KP);
            }
        }
    }
    /** pomocne metode **/
    @Override
    public boolean validate(KPRequestDTO request) {

        if(request.getIznos()==null || request.getMerchantID()==null || request.getMerchantOrderID()==null || request.getMerchantPass()==null
                || request.getMerchantTimestamp()==null)
            return false;

        if(request.getIznos()<=0)
            return false;

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
        t.setPaymentURL("");
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
    public boolean isPaymentFinished(String token) {

        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);
        Transakcija t = paymentInfo.getTransakcija();

        if(t.getStatus()==Status.U || t.getStatus()==Status.N || t.getStatus()==Status.U_KP || t.getStatus()==Status.N_KP)
            return true;
        else return false;
    }

    @Override
    public boolean checkCredentials(String token, BuyerInfoDTO buyerInfoDTO) throws ParseException {

        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);
        if(paymentInfo==null)
            return false;

        Transakcija t = paymentInfo.getTransakcija();
        if(t==null)
            return false;

        Klijent kupac = klijentRepository.findByKartice_pan(buyerInfoDTO.getPan());
        if(kupac==null)
            if (isFromBank(buyerInfoDTO.getPan())) //ako jeste pan iz nase banke ali ne mozemo da nadjemo klijenta onda nisu dobri podaci
                return false;
            else return true;


        if(!kupac.getIme().toLowerCase().equals(buyerInfoDTO.getIme().toLowerCase().trim()))
            return false;

        if(!kupac.getPrezime().toLowerCase().equals(buyerInfoDTO.getPrezime().toLowerCase().trim()))
            return false;

        if(!kupac.getKartice().get(0).getCcv().equals(buyerInfoDTO.getCvv().trim()))
            return false;

        String expDate = buyerInfoDTO.getMesec() + "/" + buyerInfoDTO.getGodina();
        if(!kupac.getKartice().get(0).getExpDate().equals(expDate))
            return false;
        int pos = expDate.lastIndexOf("/");
        int godina = Integer.valueOf(expDate.substring(pos + 1));
        int mesec =  Integer.valueOf(expDate.substring(0, pos));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(new Date(System.currentTimeMillis()));
        Date now = format.parse (dateString);
        Date exp = format.parse("20" + godina + "-"+mesec+"-31");
        if (exp.compareTo(now) < 0) {
            System.out.println("exp je pre now, istekla kartica");
            return false;
        }


        return true;
    }

    @Override
    public PaymentInfo findByPaymentURL(String token) {
        return paymentInfoRepository.findByPaymentURL(token);
    }

    @Override
    public boolean isTransakcijaPending(String token) {
        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);
        if(paymentInfo==null)
            return true;

        Transakcija t = paymentInfo.getTransakcija();
        if(t==null)
            return true;

        if(t.getStatus()==Status.C || t.getStatus()==Status.C_PCC || t.getStatus()==Status.U_KP || t.getStatus()==Status.N_KP)
            return true;
        return false;
    }

    @Override
    public List<Transakcija> getAllTransakcije() {
        return transakcijaRepository.findAll();
    }

    @Override
    public FinishedPaymentDTO createFinishedPaymentDTO(Transakcija t) {

        PaymentInfo paymentInfo = paymentInfoRepository.findByTransakcija(t);
        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();

        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID());
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());

      if(t.getStatus()==Status.U_KP) {
            finishedPaymentDTO.setStatusTransakcije(Status.U);
            finishedPaymentDTO.setRedirectURL(t.getSuccessURL());
            save(t, Status.U);

        }else if(t.getStatus().equals(Status.N_KP)){
            finishedPaymentDTO.setStatusTransakcije(Status.N);
            finishedPaymentDTO.setRedirectURL(t.getFailedURL());
            save(t, Status.N);

        }
        else if(t.getStatus().equals(Status.E_KP)){
            finishedPaymentDTO.setStatusTransakcije(Status.E);
            finishedPaymentDTO.setRedirectURL(t.getFailedURL());
          save(t, Status.E);

        }
        return finishedPaymentDTO;
    }

    @Override
    public boolean isTokenExpired(String token) {

        PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(token);

        if(paymentInfo==null)
            return true;

        Transakcija t = paymentInfo.getTransakcija();
        if(t==null)
            return true;

        if(t.getStatus()==Status.E || t.getStatus()==Status.U || t.getStatus()==Status.N || t.getStatus()==Status.U_KP || t.getStatus()==Status.N_KP)
            return true;

        if(t!=null){
            //FIXME proveriti zasto vreme krece od ponoci
            Calendar postavljeno = Calendar.getInstance();
            postavljeno.setTime(t.getTimestamp());

            Calendar max = Calendar.getInstance();
            max.setTime(t.getTimestamp());
            max.add(Calendar.MINUTE, tokenDuration);

        	System.out.println("Postavljeno vreme: " + postavljeno.getTime().toString());
            System.out.println("Max vreme: " + max.getTime().toString());
            System.out.println("Trenutno vreme: " + new Date(System.currentTimeMillis()));

            if(max.compareTo(Calendar.getInstance())<0){
                System.out.println("Postavljeno vreme za placanje je isteklo.");
                save(t, Status.E);
                return true;
            }
            else return false;


        }else return true;
    }

    private boolean isFromBank(String pan) {
        if(pan.substring(0,6).equals(BNumber))
            return true;
        else return false;
    }

    /** transakcije **/
    @Override
     public ResponseEntity<Map> tryPayment(String token, BuyerInfoDTO buyerInfoDTO, HttpServletResponse response) throws IOException, PaymentException, NotFoundException, FundsException {

        Map<String, String> map = new HashMap<>();

        PaymentInfo paymentInfo = findByPaymentURL(token);
        if (paymentInfo == null)
            throw new PaymentException("Nema informacija o ovom tokenu.");

        Transakcija t = paymentInfo.getTransakcija();
        if (t == null)
            throw new PaymentException("Nema informacija o ovoj transakciji.");
        t.setPanPosaljioca(buyerInfoDTO.getPan());//pokusano da se plati sa ove kartice

        if (isTokenExpired(token)) {
            //TODO srediti posle da automatski radi
            System.out.println("Token " + token + " je istekao.");
            save(t, Status.E);
            paymentFailed(paymentInfo, t, token, buyerInfoDTO, false);
            map.put("Location", "/expired");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        Klijent kupac = klijentRepository.findByKartice_pan(buyerInfoDTO.getPan());
        if (kupac == null) {
            if (isFromBank(buyerInfoDTO.getPan()))
                throw new NotFoundException();

            System.out.println("Kupac nije u istoj banci, kontaktiramo PCC.");
            sendToPCC(t, token, buyerInfoDTO, paymentInfo, response);
            map.put("Location", "/paymentSent");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        System.out.println("Kupac je u istoj banci.");
        List<Kartica> match = kupac.getKartice().stream()
                .filter(s -> buyerInfoDTO.getPan().equals(s.getPan()))
                .collect(Collectors.toList());

        if (match.isEmpty()) {
            System.out.println("Greska u pronalazenju kartice.");
            save(t, Status.N);
            paymentFailed(paymentInfo, t, token, buyerInfoDTO, false);
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if (match.get(0).getRaspolozivaSredstva() - t.getIznos() < 0)
            throw new FundsException();

        Kartica zaPlacanje = null;
        for(Kartica k : kupac.getKartice())
            if(k.getPan().equals(buyerInfoDTO.getPan())){
                zaPlacanje = k;
                break;
            }

        int idx = kupac.getKartice().indexOf(zaPlacanje);
        if(idx == -1)
            throw new PaymentException("Nije moguce naci karticu.");

        String location = paymentSuccessful(paymentInfo, t, token, buyerInfoDTO);
		return placanjeIstaBanka(t, zaPlacanje, kupac, paymentInfo, token, buyerInfoDTO, location);

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public ResponseEntity placanjeIstaBanka(Transakcija t, Kartica zaPlacanje,Klijent kupac, PaymentInfo paymentInfo, String token, BuyerInfoDTO buyerInfoDTO, String location ) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();

        int idx = kupac.getKartice().indexOf(zaPlacanje);
        System.out.println("Prebacivanje sredstava: " + t.getIznos());
        System.out.println("Stanje pre: " + zaPlacanje.getRaspolozivaSredstva());
        Float raspolozivo = zaPlacanje.getRaspolozivaSredstva();
        zaPlacanje.setRaspolozivaSredstva(raspolozivo - t.getIznos());
        karticaRepository.save(zaPlacanje);
        kupac.getKartice().set(idx, zaPlacanje);
        klijentRepository.save(kupac);
        System.out.println("Stanje posle: " + zaPlacanje.getRaspolozivaSredstva());

        Kartica primalac = karticaRepository.findByPan(t.getPanPrimaoca());
        raspolozivo = primalac.getRaspolozivaSredstva();
        primalac.setRaspolozivaSredstva(raspolozivo + t.getIznos());
        karticaRepository.save(primalac);

        Klijent prodavac = klijentRepository.findByKartice_pan(primalac.getPan());
        idx = prodavac.getKartice().indexOf(primalac);
        prodavac.getKartice().set(idx, primalac);
        klijentRepository.save(prodavac);

        t.setStatus(Status.U);
        transakcijaRepository.save(t);

        System.out.println("Uspesno prebacivanje novca.");


        System.out.println("Lokacija:  " + location);
        map.put("Location", location);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", location);
        headers.add("Access-Control-Allow-Origin", "*");
        return new ResponseEntity<>(map, headers, HttpStatus.OK);
    }

    @Override
    public void sendToPCC(Transakcija t, String token, BuyerInfoDTO buyerInfoDTO, PaymentInfo paymentInfo, HttpServletResponse resp) throws JsonProcessingException {

        save(t, Status.C);

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

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity responseEntity = template.postForEntity(requestToPCC, pccRequestDTO, PCCRequestDTO.class);

        }catch (HttpStatusCodeException exception) {
            if(exception.getStatusCode().is5xxServerError()){
                System.out.println("Poslata vec postojeca transakcija na PCC");
                save(t, Status.N);
            }
        }catch(Exception e){
            System.out.println("Greska kod slanja zahteva na PCC.");
            save(t, Status.C_PCC);
            PCCRequest pccRequest = new PCCRequest(pccRequestDTO);
            pccRequestRepository.save(pccRequest);
            System.out.println("Sacuvan PCCRequest:" + pccRequest.toString());
        }

    }

    @Override
    public String paymentFailed(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO, boolean rollback) throws JsonProcessingException {

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.N);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        finishedPaymentDTO.setRedirectURL(t.getFailedURL());
        save(t, Status.N);

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try{//TODO vrati success url ili failed url od kp
            ResponseEntity<Boolean> response = template.postForEntity(replyToKP, finishedPaymentDTO, Boolean.class);
            if(response.getBody())
               return t.getFailedURL();
            else return "/failed";
        }catch(Exception e){
            System.out.println("KP nije dostupan.");
            e.printStackTrace();
            save(t, Status.N_KP);
            return "/failed";
        }

    }

    @Override
    public String paymentSuccessful(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException {

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(Status.U);
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        finishedPaymentDTO.setRedirectURL(t.getSuccessURL());
        System.out.println(finishedPaymentDTO.toString());
        
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {//TODO vrati success url ili failed url od kp
        	System.out.println("*** "+replyToKP);
            ResponseEntity<Boolean> response = template.postForEntity(replyToKP, finishedPaymentDTO, Boolean.class);
            if(response.getBody())
                return t.getSuccessURL();
            else return t.getErrorURL();
        } catch(Exception e) {
            System.out.println("KP nije dostupan.");
            e.printStackTrace();
            save(t, Status.U_KP);
            return "/paymentSent";
        }

    }

    @Override
    public void finalizePayment(PCCReplyDTO pccReplyDTO) throws NotFoundException {

        Transakcija t = transakcijaRepository.findById(pccReplyDTO.getAcquirerOrderID()).get();

        if (t == null)
            throw new NotFoundException();

        PaymentInfo paymentInfo = paymentInfoRepository.findByTransakcija(t);

        if (paymentInfo == null)
            throw new NotFoundException();

        if(pccReplyDTO.getStatus()==Status.N) {
            save(t, Status.N);
        }
        else{
            t.setIssuerOrderId(pccReplyDTO.getIssuerOrderID());
            t.setIssuerTimestamp(pccReplyDTO.getIssuerTimestamp());
            save(t, Status.U);
            Kartica primalac = karticaRepository.findByPan(t.getPanPrimaoca());

            Float raspolozivo = primalac.getRaspolozivaSredstva();
            primalac.setRaspolozivaSredstva(raspolozivo + t.getIznos());
            karticaRepository.save(primalac);

            Klijent prodavac = klijentRepository.findByKartice_pan(primalac.getPan());
            int idx = prodavac.getKartice().indexOf(primalac);
            prodavac.getKartice().set(idx, primalac);
            klijentRepository.save(prodavac);

        }
        System.out.println("Novi status transakcije ID: " + t.getOrderID() + " je " + t.getStatus().toString());
        sendReplyToKP(t, paymentInfo);
    }

    private void sendReplyToKP(Transakcija t, PaymentInfo paymentInfo){

        FinishedPaymentDTO finishedPaymentDTO = new FinishedPaymentDTO();
        finishedPaymentDTO.setStatusTransakcije(t.getStatus());
        finishedPaymentDTO.setMerchantOrderID(t.getMerchantOrderId());
        finishedPaymentDTO.setAcquirerOrderID(t.getOrderID()); //ista banka
        finishedPaymentDTO.setAcquirerTimestamp(t.getTimestamp());
        finishedPaymentDTO.setPaymentID(paymentInfo.getPaymentID());
        if(t.getStatus()==Status.U)
            finishedPaymentDTO.setRedirectURL(t.getSuccessURL());
        else finishedPaymentDTO.setRedirectURL(t.getFailedURL());

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(replyToKP, finishedPaymentDTO, Boolean.class);
        }catch(Exception e){
            System.out.println("KP nije dostupan,metoda sendReplyToKP.");
            if(t.getStatus()==Status.U)
                save(t, Status.U_KP);
            else  save(t, Status.N_KP);


        }

    }
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void save(Transakcija t, Status s){
        t.setStatus(s);
        transakcijaRepository.save(t);
    }
    
    
    
  
}
