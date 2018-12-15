package sep.tim18.pcc.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCReplyDTO;
import sep.tim18.pcc.model.dto.PCCRequestDTO;
import sep.tim18.pcc.model.enums.Status;
import sep.tim18.pcc.repository.BankaRepository;
import sep.tim18.pcc.repository.ZahtevRepository;
import sep.tim18.pcc.service.MainService;

import javax.net.ssl.HttpsURLConnection;
import java.util.Date;

@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private BankaRepository bankaRepository;


    @Autowired
    private ZahtevRepository zahtevRepository;

    @Override
    public Banka getBankaByPan(String pan) {
        String brojBanke = pan.substring(0,6);
        return bankaRepository.findByBrojBanke(brojBanke);

    }

    @Override
    public Banka getBanka(String brBanke) {
        return bankaRepository.findByBrojBanke(brBanke);
    }


    @Override
    public void forward(Zahtev zahtev, PCCRequestDTO pccRequestDTO, String url) throws JsonProcessingException {
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        template.postForEntity(url, pccRequestDTO, PCCRequestDTO.class);

    }

    @Override
    public Zahtev createZahtev(PCCRequestDTO request) {
        Banka prodavca = bankaRepository.findByBrojBanke(request.getBrojBankeProdavca());
        Banka kupca = bankaRepository.findByBrojBanke(request.getPan().substring(0,6));
        Zahtev zahtev = new Zahtev();
        zahtev.setStatus(Status.K);
        zahtev.setAcquirerOrderID(request.getAcquirerOrderID());
        zahtev.setBankaKupca(kupca);
        zahtev.setBankaProdavca(prodavca);
        zahtev.setVremeKreiranja(new Date(System.currentTimeMillis()));
        zahtev.setReturnURL(request.getReturnURL());
        return zahtev;
    }


    @Override
    public void finish(PCCReplyDTO replyDTO) {

        Zahtev z = zahtevRepository.findByAcquirerOrderID(replyDTO.getAcquirerOrderID());
        if(replyDTO.getStatus()==Status.N)
            z.setStatus(Status.N);
        else
            z.setStatus(Status.U);

        zahtevRepository.save(z);
        sendReply(replyDTO, z.getReturnURL());
    }


    @Override
    public void sendReply(PCCReplyDTO pccReplyDTO, String returnURL) {

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        template.postForEntity(returnURL, pccReplyDTO, PCCReplyDTO.class);
    }
}
