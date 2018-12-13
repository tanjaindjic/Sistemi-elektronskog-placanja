package sep.tim18.banka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import reactor.core.publisher.Mono;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.PCCRequestDTO;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.TransakcijaRepository;
import sep.tim18.banka.service.IssuerService;

@RestController
public class IssuerController {

    @Autowired
    private IssuerService issuerService;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @RequestMapping(value = "/paymentRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> request(@RequestBody PCCRequestDTO request) throws JsonProcessingException {

        Klijent k = klijentRepository.findByKartice_pan(request.getPan());
        if (k == null)
            return Mono.just(new ResponseEntity(HttpStatus.BAD_REQUEST));

        Transakcija t = issuerService.createTransakcija(request, k);
        transakcijaRepository.save(t);
        return issuerService.tryPayment(request, t, k);

    }
}