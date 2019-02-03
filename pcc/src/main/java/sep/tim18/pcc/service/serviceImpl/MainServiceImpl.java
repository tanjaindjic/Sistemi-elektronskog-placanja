package sep.tim18.pcc.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.PCCRequest;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCReplyDTO;
import sep.tim18.pcc.model.dto.PCCRequestDTO;
import sep.tim18.pcc.model.enums.Status;
import sep.tim18.pcc.repository.BankaRepository;
import sep.tim18.pcc.repository.PCCRequestRepository;
import sep.tim18.pcc.repository.ZahtevRepository;
import sep.tim18.pcc.service.MainService;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private BankaRepository bankaRepository;

    @Autowired
    private PCCRequestRepository pccRequestRepository;

    @Autowired
    private ZahtevRepository zahtevRepository;

    private static String B2Sync;

    @Value("${B2Sync}")
    public void setBURL(String bank2s) {
        B2Sync = bank2s;
    }

    @Scheduled(initialDelay = 5000, fixedRate = 30000)
    public void getRemainingTransactions() {
        System.out.println("SCHEDULED TASK---Kontaktira ISSUERA da dobavi nedospele transakcije. ");
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<List<PCCReplyDTO>> response = restTemplate.exchange(B2Sync, HttpMethod.GET, null, new ParameterizedTypeReference<List<PCCReplyDTO>>() {});
            List<PCCReplyDTO> preostaleTransakcije = response.getBody();
            System.out.println("SCHEDULED TASK---Primio od ISSUERA: " + preostaleTransakcije.size());
            for (PCCReplyDTO pccReplyDTO : preostaleTransakcije) {
                Zahtev z = zahtevRepository.findByMerchantOrderId(pccReplyDTO.getMerchantOrderID());
                if (pccReplyDTO.getStatus().equals(Status.U))
                    z.setStatus(Status.U_B1);//posto je za ove transakcije pukla konekcija, nije nista javljeno ni acquireru pa ce on
                    //pokupiti ove transakcije sledeci put kad posalje zahtev za preostale transakcije koje su do tad bile na cekanju
                else z.setStatus(Status.N_B1);
                zahtevRepository.save(z);
                System.out.println("Novi status zahteva ID: " + z.getId() + " je " + z.getStatus().toString());
            }
        } catch (Exception e){
        System.out.println("SCHEDULED TASK---ISSUER nije dostupan.");
    }
    }
    @Scheduled(initialDelay = 5000, fixedRate = 30000)
    public void sendRemainingTransactions() throws JsonProcessingException {
        System.out.println("SCHEDULED TASK---Usao u slanje neposlatih transakcija za ISSUERA");
        if(zahtevRepository.findByStatus(Status.C_B2).isEmpty()){
            System.out.println("SCHEDULED TASK---Nema neposlatih zahteva.");
            return;
        }
        RestTemplate restTemplate = new RestTemplate();
        for(Zahtev z : zahtevRepository.findByStatus(Status.C_B2)){

            PCCRequest pccRequest = pccRequestRepository.findByMerchantOrderID(z.getMerchantOrderId());
            Banka odKupca = getBankaByPan(pccRequest.getPanPosaljioca());
            PCCRequestDTO pccRequestDTO = new PCCRequestDTO(pccRequest);
            System.out.println("SCHEDULED TASK---Salje zahtev " + z.getId() + " za ISSUERA");
            forward(z, pccRequestDTO,odKupca.getUrlBanke());
        }

    }

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
        zahtev.setStatus(Status.C);
        zahtevRepository.save(zahtev);

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(url, pccRequestDTO, PCCRequestDTO.class);
            PCCRequest pccRequest = pccRequestRepository.findByMerchantOrderID(pccRequestDTO.getMerchantOrderID());
            if( pccRequest!=null )
                pccRequestRepository.delete(pccRequest);
        }catch(Exception e){
            System.out.println("Greska kod slanja zahteva na ISSUERA.");
            zahtev.setStatus(Status.C_B2);
            if(pccRequestRepository.findByMerchantOrderID(pccRequestDTO.getMerchantOrderID())==null) {
                PCCRequest pccRequest = new PCCRequest(pccRequestDTO);
                pccRequestRepository.save(pccRequest);
            }
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
        zahtev.setMerchantOrderId(request.getMerchantOrderID());
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
        System.out.println("Novi status zahteva ID: " + z.getId() + " je " + z.getStatus().toString());
        sendReply(replyDTO, z);
    }

    @Override
    public Zahtev checkRequest(@Valid PCCRequestDTO request) {
        //moze vise puta da se proba naplata za istu transakciju, mozda je n puta neuspela pa oept probamo dok ne istekne token
        Zahtev zahtev = zahtevRepository.findByAcquirerOrderID(request.getAcquirerOrderID());

        if(zahtev==null)
            return createZahtev(request);
        else return null;

    }

    @Override
    public void sendReply(PCCReplyDTO pccReplyDTO, Zahtev zahtev) {

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(zahtev.getReturnURL(), pccReplyDTO, PCCReplyDTO.class);
        }catch(Exception e){
            System.out.println("Greska kod slanja odgovora za ACQUIRERA.");
            zahtev.setIssuerOrderID(pccReplyDTO.getIssuerOrderID());
            zahtev.setIssuerTimestamp(pccReplyDTO.getIssuerTimestamp());
            if(pccReplyDTO.getStatus()==Status.N)
                zahtev.setStatus(Status.N_B1);
            else zahtev.setStatus(Status.U_B1);
            zahtevRepository.save(zahtev);
        }
    }

}
