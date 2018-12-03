package sep.tim18.pcc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCRequestDTO;
import sep.tim18.pcc.model.enums.Status;
import sep.tim18.pcc.repository.ZahtevRepository;
import sep.tim18.pcc.service.MainService;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @Autowired
    private ZahtevRepository zahtevRepository;

    @RequestMapping(value = "/request", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity request(@RequestBody PCCRequestDTO request) throws JsonProcessingException {
        Zahtev zahtev = mainService.createZahtev(request);
        Banka odKupca = mainService.getBanka(request.getPan());

        if(odKupca==null){ //nema kojoj banci da posalje
            zahtev.setStatus(Status.N);
            zahtevRepository.save(zahtev);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }else{
            zahtev.setBankaKupca(odKupca);
            zahtev.setStatus(Status.P);
            zahtevRepository.save(zahtev);
            return mainService.forward(zahtev, request, odKupca.getUrlBanke()); //npr /requestPayment

        }


    }


}
