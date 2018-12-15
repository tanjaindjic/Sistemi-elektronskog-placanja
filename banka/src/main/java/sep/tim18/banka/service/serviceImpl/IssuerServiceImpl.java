package sep.tim18.banka.service.serviceImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
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

        Kartica kartica = karticaRepository.findByPan(request.getPan());
        Transakcija t = new Transakcija();
        t.setUplacuje(k);
        t.setPrima(null);
        t.setPaymentURL("");
        t.setTimestamp(new Date());
        t.setStatus(Status.K);
        t.setRacunPrimaoca(request.getRacunPrimaoca());
        t.setRacunPosiljaoca(kartica.getBrRacuna());
        t.setIznos(request.getIznos());
        t.setErrorURL("");
        t.setFailedURL("");
        t.setSuccessURL("");
        t.setMerchantOrderId(request.getMerchantOrderID());
        t.setMerchantTimestamp(request.getMerchantTimestamp());
        return t;

    }

    @Override
    public void tryPayment(PCCRequestDTO request, Transakcija t, Klijent k) throws JsonProcessingException {

        PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
        pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
        Kartica kartica = karticaRepository.findByBrRacuna(t.getRacunPosiljaoca());
        int idx = k.getKartice().indexOf(kartica);
        if(kartica.getRaspolozivaSredstva()-t.getIznos()<0){
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
        sendReply(pccReplyDTO);

    }

    @Override
    public void sendReply(PCCReplyDTO reply) {
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(replyToPCC, reply, PCCReplyDTO.class);
        }catch (Exception e){
            System.out.println("KP nedostupan");
        }
    }
}
