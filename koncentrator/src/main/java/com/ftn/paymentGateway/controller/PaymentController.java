package com.ftn.paymentGateway.controller;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
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

import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.dto.PaymentResponseDTO;
import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.InvalidPaymentTypeException;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.exceptions.TransactionUpdateExeption;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentFactory;
import com.ftn.paymentGateway.paymentStrategy.impl.PayPalPayment;
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
	@Autowired 
	private PayPalPayment payPalPayment;
	
	
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
	public ResponseEntity<Boolean> doPayment(
			@RequestParam(value="paymentTypeId", required = true) Long paymentTypeId,
			@RequestParam(value="uniqueToken", required = true) String uniqueToken){
		
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
		
		TransakcijaIshodDTO retVal = null;
		try {
			retVal = paymentFactory.getPaymentStrategy(tipPlacanja).doPayment(transakcija, podrzanoPlacanje);
		} catch (InvalidPaymentTypeException | PaymentErrorException e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		try {
			transakcijaService.update(retVal, transakcija);
		} catch (TransactionUpdateExeption e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(retVal.isRedirekcija()) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Location", retVal.getNovaPutanja());
			headers.add("Access-Control-Allow-Origin", "*");
			return new ResponseEntity<Boolean>(retVal.isUspesno(), headers, HttpStatus.OK);
		}
	    
		return new ResponseEntity<Boolean>(retVal.isUspesno(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "success", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaymentResponseDTO> completePayment(HttpServletRequest request, @RequestParam("paymentId") String paymentId, @RequestParam("payerId") String payerId, @RequestParam("token") String token){
		Transakcija transakcija = transakcijaService.findByIzvrsnaTransakcija(Long.decode(request.getParameter("paymentId")));
		TipPlacanja tipPlacanja = tipPlacanjaService.getByKod("PPP");
		ArrayList<PodrzanoPlacanje> podrzanaPlacanja = podrzanoPlacanjeService.getByEntitetPlacanjaAndTipPlacanja(transakcija.getEntitetPlacanja(), tipPlacanja);
		if(podrzanaPlacanja.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		PodrzanoPlacanje podrzanoPlacanje = podrzanaPlacanja.get(0);
		
        Boolean uspesno = payPalPayment.completePayment(request, podrzanoPlacanje);
        PaymentResponseDTO response = new PaymentResponseDTO();
        if(!uspesno){
            response.setPoruka("PayPal uplata je uspesno izvrsena");
            response.setStatus(TransakcijaStatus.N);
            transakcija.setStatus(TransakcijaStatus.N);
        }
        response.setMaticnaTransakcija(transakcija.getMaticnaTransakcija());
        response.setPoruka("PayPal uplata je uspesno izvrsena");
        response.setStatus(TransakcijaStatus.U);
        System.out.println("NINA CAREEEEE");
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
