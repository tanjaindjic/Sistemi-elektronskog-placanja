package sep.tim18.banka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.PCCReplyDTO;
import sep.tim18.banka.model.dto.PCCRequestDTO;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.TransakcijaRepository;
import sep.tim18.banka.service.IssuerService;

import javax.validation.Valid;

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
    public void request(@Valid @RequestBody PCCRequestDTO request) throws JsonProcessingException {

        System.out.println("Zahtev od PCC-a: " + request.toString());
        issuerService.startPayment(request);

    }
}