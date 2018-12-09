package sep.tim18.banka.serviceImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

@Service
public class IssuerServiceImpl implements IssuerService {

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KarticaRepository karticaRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    @Override
    public Transakcija createTransakcija(PCCRequestDTO request, Klijent k) {

        Kartica kartica = karticaRepository.findByPan(request.getPan());
        Transakcija t = new Transakcija();
        t.setUplacuje(k);
        t.setPrima(null);
        t.setPaymentURL(null);
        t.setTimestamp(new Date());
        t.setStatus(Status.K);
        t.setRacunPrimaoca(request.getRacunPrimaoca());
        t.setRacunPosiljaoca(kartica.getBrRacuna());
        t.setIznos(request.getIznos());
        return t;

    }

    @Override
    public ResponseEntity<String> tryPayment(PCCRequestDTO request, Transakcija t, Klijent k) throws JsonProcessingException {

        Kartica kartica = karticaRepository.findByBrRacuna(t.getRacunPosiljaoca());
        int idx = k.getKartice().indexOf(kartica);
        if(kartica.getRaspolozivaSredstva()-t.getIznos()<0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Float raspolozivo = kartica.getRaspolozivaSredstva();
        kartica.setRaspolozivaSredstva(raspolozivo - t.getIznos());
        karticaRepository.save(kartica);
        k.getKartice().set(idx, kartica);
        klijentRepository.save(k);

        PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
        pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
        pccReplyDTO.setAcquirerTimestamp(request.getAcquirerTimestamp());
        pccReplyDTO.setIssuerOrderID(t.getOrderID());
        pccReplyDTO.setIssuerTimestamp(t.getTimestamp());

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(pccReplyDTO);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(jsonInString, headers, HttpStatus.OK);


    }
}
