package sep.tim18.pcc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCReplyDTO;
import sep.tim18.pcc.model.dto.PCCRequestDTO;
import sep.tim18.pcc.model.enums.Status;
import sep.tim18.pcc.repository.ZahtevRepository;
import sep.tim18.pcc.service.MainService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @Autowired
    private ZahtevRepository zahtevRepository;

    @RequestMapping(value = "/request", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity.BodyBuilder request(@Valid @RequestBody PCCRequestDTO request) throws JsonProcessingException {

        Zahtev zahtev = mainService.checkRequest(request);
        if(zahtev == null){
            System.out.println("Zahtev odbijen jer vec postoji za tu transakciju.");
            return ResponseEntity.badRequest();
        }

        Banka odKupca = mainService.getBankaByPan(request.getPanPosaljioca());

        if(odKupca==null){ //nema kojoj banci da posalje
            zahtev.setStatus(Status.N);
            zahtevRepository.save(zahtev);

            PCCReplyDTO pccReplyDTO = new PCCReplyDTO();
            pccReplyDTO.setAcquirerOrderID(request.getAcquirerOrderID());
            pccReplyDTO.setStatus(Status.N);
            mainService.sendReply(pccReplyDTO, zahtev);

        }else mainService.forward(zahtev, request, odKupca.getUrlBanke()); //npr /requestPayment

        return ResponseEntity.ok();
    }

    @RequestMapping(value = "/reply", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reply(@Valid @RequestBody PCCReplyDTO replyDTO){
        System.out.println("PCC primio odgovor: " + replyDTO.toString());
        mainService.finish(replyDTO);
    }

    @RequestMapping(value = "/getTransactions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getTransactions(){
        List<PCCReplyDTO> transakcije = new ArrayList<>();
        for(Zahtev z : zahtevRepository.findAll()) {
            if (z.getStatus().equals(Status.N_B1)) {
                PCCReplyDTO pccReplyDTO = new PCCReplyDTO(z.getIssuerOrderID(), z.getIssuerTimestamp(), Status.N, z.getAcquirerOrderID(), z.getMerchantOrderId());
                z.setStatus(Status.N);
                zahtevRepository.save(z);
                transakcije.add(pccReplyDTO);
            } else if (z.getStatus().equals(Status.U_B1)) {
                PCCReplyDTO pccReplyDTO = new PCCReplyDTO(z.getIssuerOrderID(), z.getIssuerTimestamp(), Status.U, z.getAcquirerOrderID(), z.getMerchantOrderId());
                z.setStatus(Status.U);
                zahtevRepository.save(z);
                transakcije.add(pccReplyDTO);
            } else if (z.getStatus().equals(Status.C) || z.getStatus().equals(Status.C_B2)) {
                PCCReplyDTO pccReplyDTO = new PCCReplyDTO(z.getIssuerOrderID(), z.getIssuerTimestamp(), Status.C, z.getAcquirerOrderID(), z.getMerchantOrderId());
                transakcije.add(pccReplyDTO);
            }
        }
        return new ResponseEntity<>(transakcije, HttpStatus.OK);
    }


}
