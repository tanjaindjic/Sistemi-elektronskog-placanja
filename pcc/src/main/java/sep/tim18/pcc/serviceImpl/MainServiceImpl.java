package sep.tim18.pcc.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCRequestDTO;
import sep.tim18.pcc.model.enums.Status;
import sep.tim18.pcc.repository.BankaRepository;
import sep.tim18.pcc.repository.ZahtevRepository;
import sep.tim18.pcc.service.MainService;

@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private BankaRepository bankaRepository;


    @Autowired
    private ZahtevRepository zahtevRepository;

    @Override
    public Banka getBanka(String pan) {
        String brojBanke = pan.substring(0,6);
        return bankaRepository.findByBrojBanke(brojBanke);

    }

    @Override
    public ResponseEntity forward(Zahtev zahtev, PCCRequestDTO pcCrequestDTO, String url) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(pcCrequestDTO);
        HttpStatus statusCode = null;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(jsonInString, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        statusCode = response.getStatusCode();
        if (statusCode == HttpStatus.OK){
            //ako je banka kupca uspesno obradila transakciju
            zahtev.setStatus(Status.U);
            zahtevRepository.save(zahtev);
            return new ResponseEntity(HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST); //banka kupca nije obradial transakciju
    }

    @Override
    public Zahtev createZahtev(PCCRequestDTO request) {
        Banka prodavca = bankaRepository.findByBrojBanke(request.getBrojBankeProdavca());
        Banka kupca = bankaRepository.findByBrojBanke(request.getPan().substring(0,6));
        Zahtev zahtev = new Zahtev();
        zahtev.setStatus(Status.P);
        zahtev.setAcquirerOrderID(request.getAcquirerOrderID());
        zahtev.setBankaKupca(kupca);
        zahtev.setBankaProdavca(prodavca);
        zahtev.setVremeKreiranja(new DateTime());

        return zahtev;
    }
}
