package sep.tim18.pcc.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
import javax.validation.Valid;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
    public List<Zahtev> getZahtevi(Long acquirerOrderID) {
        return zahtevRepository.findByAcquirerOrderID(acquirerOrderID);
    }

    @Override
    public void forward(Zahtev zahtev, PCCRequestDTO pccRequestDTO, String url) throws JsonProcessingException {
        zahtev.setStatus(Status.C);
        zahtevRepository.save(zahtev);

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(url, pccRequestDTO, PCCRequestDTO.class);
        }catch(Exception e){
            System.out.println("Greska kod slanja zahteva na banku kupca.");
            zahtev.setStatus(Status.C_B);
            zahtevRepository.save(zahtev);
        }

    }

    @Override
    public Zahtev createZahtev(PCCRequestDTO request) {

        Banka prodavca = bankaRepository.findByBrojBanke(request.getBrojBankeProdavca());
        Banka kupca = bankaRepository.findByBrojBanke(request.getPanPosaljioca().substring(0,6));
        Zahtev zahtev = new Zahtev();
        zahtev.setStatus(Status.K);
        zahtev.setAcquirerOrderID(request.getAcquirerOrderID());
        zahtev.setAcquirerTimestamp(request.getAcquirerTimestamp());
        zahtev.setBankaKupca(kupca);
        zahtev.setBankaProdavca(prodavca);
        zahtev.setVremeKreiranja(new Date(System.currentTimeMillis()));
        zahtev.setReturnURL(request.getReturnURL());
        return zahtev;
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void finish(PCCReplyDTO replyDTO) {

        Zahtev z = zahtevRepository.findByAcquirerOrderIDAndStatus(replyDTO.getAcquirerOrderID(), Status.C);
        if(z==null)
            return;
        if(replyDTO.getStatus()==Status.N)
            z.setStatus(Status.N);
        else
            z.setStatus(Status.U);

        zahtevRepository.save(z);
        sendReply(replyDTO, z);
    }

    @Override
    public Zahtev checkRequest(@Valid PCCRequestDTO request) {
        //moze vise puta da se proba naplata za istu transakciju, mozda je n puta neuspela pa oept probamo dok ne istekne token
        List<Zahtev> zahtevi = getZahtevi(request.getAcquirerOrderID());

        if(zahtevi.isEmpty())
            return createZahtev(request);

        Zahtev zahtev = zahtevi.stream().max(Comparator.comparing(Zahtev::getVremeKreiranja)).get();

        //ili cekam odgovor od issuera pa odbijam ili je zahtev obradjen pa odbijam zato sto cu svakako poslati
        //odgovor kada budem prolazila kroz sve neposlate zahteve
        if(zahtev.getStatus()==Status.C_B || zahtev.getStatus()==Status.C || zahtev.getStatus()==Status.U_B)
            return null;

        //ako je poslednja naplata neuspesna, kreiram novi zahtev da se proba ponovo sa naplatom
        if(zahtev.getStatus()==Status.N)
            return createZahtev(request);

        //znaci da je takodje neuspela naplata samo pcc nije uspeo da javi prodavcu
        //pustamo da se proba placanje ponovo ali radimo samo sa poslednjim zahtevom
         if(zahtev.getStatus()==Status.N_B) {
             zahtev.setStatus(Status.N);
             zahtevRepository.save(zahtev);
             return createZahtev(request);
         }

        if(zahtev.getStatus()==Status.K)
            return zahtev;

        return null;
    }

    @Override
    public void sendReply(PCCReplyDTO pccReplyDTO, Zahtev zahtev) {

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(zahtev.getReturnURL(), pccReplyDTO, PCCReplyDTO.class);
        }catch(Exception e){
            System.out.println("Greska kod slanja odgovora na banku prodavca.");
            zahtev.setIssuerOrderID(pccReplyDTO.getIssuerOrderID());
            zahtev.setIssuerTimestamp(pccReplyDTO.getIssuerTimestamp());
            if(pccReplyDTO.getStatus()==Status.N)
                zahtev.setStatus(Status.N_B);
            else zahtev.setStatus(Status.U_B);
            zahtevRepository.save(zahtev);
        }
    }
}
