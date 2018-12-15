package sep.tim18.pcc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCReplyDTO;
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

    @RequestMapping(value = "/request", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void request(@RequestBody PCCRequestDTO request) throws JsonProcessingException {

        Zahtev zahtev = mainService.createZahtev(request);
        Banka odKupca = mainService.getBankaByPan(request.getPanPosaljioca());
        Banka odProdavca = mainService.getBanka(request.getBrojBankeProdavca());
        zahtev.setBankaProdavca(odProdavca);

        if(odKupca==null){ //nema kojoj banci da posalje
            zahtev.setStatus(Status.N);
            zahtevRepository.save(zahtev);

            PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
            pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
            pccReplyDTO.setStatus(Status.N);
            mainService.sendReply(pccReplyDTO, zahtev.getReturnURL());
        }else{
            zahtev.setBankaKupca(odKupca);
            zahtev.setStatus(Status.C);
            zahtevRepository.save(zahtev);
            mainService.forward(zahtev, request, odKupca.getUrlBanke()); //npr /requestPayment
        }


    }

    @RequestMapping(value = "/reply", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reply(@RequestBody PCCReplyDTO replyDTO){
        System.out.println("PCC primio odgovor: " + replyDTO.toString());
        mainService.finish(replyDTO);
    }


}
