package sep.tim18.banka.service.serviceImpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sep.tim18.banka.exceptions.PaymentException;
import sep.tim18.banka.model.Kartica;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.PCCReplyDTO;
import sep.tim18.banka.model.dto.PCCRequestDTO;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.repository.KarticaRepository;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.TransakcijaRepository;
import sep.tim18.banka.service.IssuerService;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.Valid;

@Service
public class IssuerServiceImpl implements IssuerService {

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KarticaRepository karticaRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    private static String replyToPCC;

    @Value("${replyToPCC}")
    public void setsss(String s) {
        replyToPCC = s;
    }

    @Override
    public Transakcija createTransakcija(PCCRequestDTO request, Klijent k) {

        Kartica kartica = karticaRepository.findByPan(request.getPanPosaljioca());

        Transakcija t = new Transakcija();
        t.setUplacuje(k);
        t.setPrima(null);
        t.setPaymentURL("");
        t.setTimestamp(new Date());
        t.setStatus(Status.K);
        t.setPanPrimaoca(request.getPanPrimaoca());
        if(kartica!=null)
            t.setPanPosaljioca(kartica.getPan());
        t.setIznos(request.getIznos());
        t.setErrorURL("");
        t.setFailedURL("");
        t.setSuccessURL("");
        t.setMerchantOrderId(request.getMerchantOrderID());
        t.setMerchantTimestamp(request.getMerchantTimestamp());
        transakcijaRepository.save(t);
        return t;

    }

    @Override
    public boolean checkCredentials(PCCRequestDTO request, Klijent k) {

        if(k==null)
            return false;

        if(!k.getIme().toLowerCase().equals(request.getIme().toLowerCase().trim()))
            return false;

        if(!k.getPrezime().toLowerCase().equals(request.getPrezime().toLowerCase().trim()))
            return false;

        if(!k.getKartice().get(0).getCcv().equals(request.getCvv().trim()))
            return false;

        String expDate = request.getMesec() + "/" + request.getGodina();
        if(!k.getKartice().get(0).getExpDate().equals(expDate))
            return false;

        return true;

    }

    @Override
    public void checkPayment(@Valid PCCRequestDTO request) throws JsonProcessingException, PaymentException {

        Klijent k = klijentRepository.findByKartice_pan(request.getPanPosaljioca());
        Transakcija t = createTransakcija(request, k);

        if (k != null) {
            if (checkCredentials(request, k)) {
                processPayment(request, t, k);
            }else {
                System.out.println("Podaci kupca nisu validni.");
                save(t, Status.N);
                PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
                pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
                pccReplyDTO.setStatus(Status.N);
                sendReply(pccReplyDTO, t);
            }
        }else {
            System.out.println("Nalog kupca ne postoji u trazenoj banci.");
            save(t, Status.N);
            PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
            pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
            pccReplyDTO.setMerchantOrderID(request.getMerchantOrderID());
            pccReplyDTO.setStatus(Status.N);
            sendReply(pccReplyDTO, t);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void processPayment(PCCRequestDTO request, Transakcija t, Klijent k) throws JsonProcessingException, PaymentException {

        PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
        pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
        pccReplyDTO.setMerchantOrderID(request.getMerchantOrderID());
        Kartica kartica = karticaRepository.findByPan(t.getPanPosaljioca());
        int idx = k.getKartice().indexOf(kartica);

        if (idx == -1){
            System.out.println("Transakcija neuspesna, nije pronadjena kartica.");
            t.setStatus(Status.N);
            transakcijaRepository.save(t);
            pccReplyDTO.setStatus(Status.N);
            sendReply(pccReplyDTO, t);
            return;
        }

        if(kartica.getRaspolozivaSredstva() - t.getIznos() < 0){
            System.out.println("Transakcija neuspesna, nedovoljno sredstava.");
            t.setStatus(Status.N);
            transakcijaRepository.save(t);
            pccReplyDTO.setStatus(Status.N);
            sendReply(pccReplyDTO, t);
            return;
        }

        Float raspolozivo = kartica.getRaspolozivaSredstva();
        kartica.setRaspolozivaSredstva(raspolozivo - t.getIznos());
        karticaRepository.save(kartica);

        k.getKartice().set(idx, kartica);
        klijentRepository.save(k);

        t.setStatus(Status.U);
        transakcijaRepository.save(t);

        pccReplyDTO.setIssuerOrderID(t.getOrderID());
        pccReplyDTO.setIssuerTimestamp(t.getTimestamp());
        pccReplyDTO.setStatus(Status.U);
        System.out.println("Transakcija je uspesna.");
        sendReply(pccReplyDTO, t);

    }

    @Override
    public void sendReply(PCCReplyDTO reply, Transakcija t) {

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(replyToPCC, reply, PCCReplyDTO.class);
        }catch (Exception e){
            System.out.println("PCC nedostupan.");
            if(t.getStatus()==Status.N)
                save(t, Status.N_PCC);
            else save(t, Status.U_PCC);
        }
    }

    @Override
    public List<Transakcija> getAllTransakcije() {
        return transakcijaRepository.findAll();
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void save(Transakcija t, Status s){
        t.setStatus(s);
        transakcijaRepository.save(t);
    }

}
