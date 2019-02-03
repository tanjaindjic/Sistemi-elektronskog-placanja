package sep.tim18.banka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import sep.tim18.banka.exceptions.PaymentException;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.PCCReplyDTO;
import sep.tim18.banka.model.dto.PCCRequestDTO;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.TransakcijaRepository;
import sep.tim18.banka.service.IssuerService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class IssuerController {

    @Autowired
    private IssuerService issuerService;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    private static String replyToPCC;

    @Value("${replyToPCC}")
    public void setsss(String s) {
        replyToPCC = s;
    }

    @RequestMapping(value = "/paymentRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void request(@Valid @RequestBody PCCRequestDTO request) throws JsonProcessingException, PaymentException {

        System.out.println("Zahtev od PCC-a: " + request.toString());
        issuerService.checkPayment(request);

    }

    @RequestMapping(value = "/getIssuerTransactions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getTransactions() throws IOException {
        List<PCCReplyDTO> transakcije = new ArrayList<>();
        for(Transakcija t : issuerService.getAllTransakcije())
            if(t.getStatus().equals(Status.U_PCC)){
                PCCReplyDTO pccReplyDTO = new PCCReplyDTO(t.getOrderID(), t.getTimestamp(), Status.U, null, t.getMerchantOrderId());
                t.setStatus(Status.U);
                transakcijaRepository.save(t);
                transakcije.add(pccReplyDTO);
            }else if(t.getStatus().equals(Status.N_PCC)){
                PCCReplyDTO pccReplyDTO = new PCCReplyDTO(t.getOrderID(), t.getTimestamp(), Status.N, null, t.getMerchantOrderId());
                t.setStatus(Status.N);
                transakcijaRepository.save(t);
                transakcije.add(pccReplyDTO);
            }

        return new ResponseEntity<>(transakcije, HttpStatus.OK);
    }
}