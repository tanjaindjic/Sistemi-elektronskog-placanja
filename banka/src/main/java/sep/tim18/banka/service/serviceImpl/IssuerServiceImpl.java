package sep.tim18.banka.service.serviceImpl;

import java.util.Date;

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
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void tryPayment(PCCRequestDTO request, Transakcija t, Klijent k) throws JsonProcessingException, PaymentException {

        PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
        pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());

        Kartica kartica = karticaRepository.findByPan(t.getPanPosaljioca());

        int idx = k.getKartice().indexOf(kartica);
        try {
            if (idx == -1)
                throw new PaymentException("Kartica nije pronadjena.");
        }catch (PaymentException e){
            System.out.println(e.getMessage());;
        }

        if(kartica.getRaspolozivaSredstva() - t.getIznos() < 0){
            System.out.println("Transakcija neuspesna, nedovoljno sredstava.");
            t.setStatus(Status.N);
            transakcijaRepository.save(t);
            pccReplyDTO.setStatus(Status.N);
            sendReply(pccReplyDTO);
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
        sendReply(pccReplyDTO);

    }

    @Override
    public void sendReply(PCCReplyDTO reply) {

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(replyToPCC, reply, PCCReplyDTO.class);
        }catch (Exception e){
            System.out.println("PCC nedostupan.");
        }
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
    public void startPayment(@Valid PCCRequestDTO request) throws JsonProcessingException, PaymentException {

        Klijent k = klijentRepository.findByKartice_pan(request.getPanPosaljioca());

        if (k != null) {

            if (checkCredentials(request, k)) {
                Transakcija t = createTransakcija(request, k);
                transakcijaRepository.save(t);
                tryPayment(request, t, k);
            }else {
                System.out.println("Podaci kupca nisu validni.");
                PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
                pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
                pccReplyDTO.setStatus(Status.N);
                sendReply(pccReplyDTO);
            }
        }else {
            System.out.println("Nalog kupca ne postoji u trazenoj banci.");
            PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
            pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
            pccReplyDTO.setStatus(Status.N);
            sendReply(pccReplyDTO);
        }
    }
}
