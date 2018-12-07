package com.ftn.paymentGateway.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

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
import org.springframework.web.bind.annotation.RestController;

import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.dto.PaymentResponseDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.model.EntitetPlacanja;
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

}
