package com.ftn.paymentGateway.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ftn.paymentGateway.dto.BankRequestDTO;
import com.ftn.paymentGateway.dto.BankResponseDTO;
import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.dto.PaymentResponseDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.InvalidPaymentTypeException;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentFactory;
import com.ftn.paymentGateway.service.EntitetPlacanjaService;
import com.ftn.paymentGateway.service.PodrzanoPlacanjeService;
import com.ftn.paymentGateway.service.TipPlacanjaService;
import com.ftn.paymentGateway.service.TransakcijaService;

@RestController
@RequestMapping(value = "/rest/")
public class PaymentController {
	
	@Value("${frontend.redirectURL}")
	private String redirectionUrl;
	
	@Autowired 
	private EntitetPlacanjaService entitetPlacanjaService;
	
	@Autowired
	private TipPlacanjaService tipPlacanjaService;
	
	@Autowired
	private PodrzanoPlacanjeService podrzanoPlacanjeService;
	
	@Autowired
	private TransakcijaService transakcijaService;
	
	@Autowired 
	private PaymentFactory paymentFactory;
	
	
	@RequestMapping(value = "sendPaymentRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaymentResponseDTO> recivePayment(@Valid @RequestBody PaymentRequestDTO paymentRequest, BindingResult bindingResult) throws URISyntaxException, UnsupportedEncodingException {
		
		if(bindingResult.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(!entitetPlacanjaService.validateChain(paymentRequest.getEntitetPlacanja())) {
			return new ResponseEntity<PaymentResponseDTO>(new PaymentResponseDTO(paymentRequest.getMaticnaTransakcija(), TransakcijaStatus.N, "Neispravan entitet placanja."), HttpStatus.BAD_REQUEST);
		}
		
		EntitetPlacanja ep = entitetPlacanjaService.getByIdentifikacioniKod(paymentRequest.getEntitetPlacanja().getIdentifikacioniKod());
		
		Transakcija novaTransakcija = transakcijaService.insertNewTransaction(ep, paymentRequest);
		
		if(novaTransakcija == null) {
			return new ResponseEntity<PaymentResponseDTO>(new PaymentResponseDTO(paymentRequest.getMaticnaTransakcija(), TransakcijaStatus.N, "Neuspesno placanje, pokusajte kasnije."), HttpStatus.OK);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", redirectionUrl+novaTransakcija.getJedinstveniToken());
		
		return new ResponseEntity<PaymentResponseDTO>(new PaymentResponseDTO(paymentRequest.getMaticnaTransakcija(), TransakcijaStatus.C, "Transakcija je uspesno zabelezena, bicete preusmereni na panel za placanje."), headers, HttpStatus.FOUND);
	}
	
	@RequestMapping(value = "doPayment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TransakcijaStatus> doPayment(
			@RequestParam(value="paymentTypeId", required = true) Long paymentTypeId,
			@RequestParam(value="uniqueToken", required = true) String uniqueToken){
		
		System.out.println(paymentTypeId);
		System.out.println(uniqueToken);
		
		Transakcija transakcija = transakcijaService.getByJedinstveniToken(uniqueToken);
		
		if(transakcija == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		TipPlacanja tipPlacanja = tipPlacanjaService.getById(paymentTypeId);
		
		if(tipPlacanja == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		ArrayList<PodrzanoPlacanje> podrzanaPlacanja = podrzanoPlacanjeService.getByEntitetPlacanjaAndTipPlacanja(transakcija.getEntitetPlacanja(), tipPlacanja);
		
		if(podrzanaPlacanja.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		PodrzanoPlacanje podrzanoPlacanje = podrzanaPlacanja.get(0);
		
		TransakcijaStatus retVal;
		try {
			retVal = paymentFactory.getPaymentStrategy(tipPlacanja.getKod()).doPayment(transakcija, podrzanoPlacanje);
		} catch (InvalidPaymentTypeException e) {
			System.out.println("Nevalidan tip placanja!");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	    
		return new ResponseEntity<TransakcijaStatus>(retVal, HttpStatus.OK);
	}

}
