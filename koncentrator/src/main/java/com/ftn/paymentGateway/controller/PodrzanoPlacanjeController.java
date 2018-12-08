package com.ftn.paymentGateway.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ftn.paymentGateway.dto.BankRequestDTO;
import com.ftn.paymentGateway.dto.BankResponseDTO;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.service.PodrzanoPlacanjeService;
import com.ftn.paymentGateway.service.TipPlacanjaService;
import com.ftn.paymentGateway.service.TransakcijaService;

@RestController
@RequestMapping(value = "/rest/")
public class PodrzanoPlacanjeController {
	
	@Autowired
	private TipPlacanjaService tipPlacanjaService;
	
	@Autowired
	private PodrzanoPlacanjeService podrzanoPlacanjeService;
	
	@Autowired
	private TransakcijaService transakcijaService;
	
	@RequestMapping(value = "getSupportedPaymentTypes/{uniqueToken}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<TipPlacanja>> getSupportedPaymentTypes(@PathVariable String uniqueToken){
		
		Transakcija transakcija = transakcijaService.getByJedinstveniToken(uniqueToken);
		
		if(transakcija == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		ArrayList<PodrzanoPlacanje> podrzanaPlacanja = podrzanoPlacanjeService.getByEntitetPlacanja(transakcija.getEntitetPlacanja());
		ArrayList<TipPlacanja> retVal = new ArrayList<TipPlacanja>();
		
		for(PodrzanoPlacanje pp : podrzanaPlacanja) {
			retVal.add(pp.getTipPlacanja());
		}
		
		return new ResponseEntity<ArrayList<TipPlacanja>>(retVal, HttpStatus.OK);
	}
	
	@RequestMapping(value = "sendRedirectToBanka/{uniqueToken}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sendRedirectToBanka(HttpServletRequest arg0, HttpServletResponse arg1, @PathVariable String uniqueToken){
		Transakcija transakcija = transakcijaService.getByJedinstveniToken(uniqueToken);
		
		if(transakcija == null) {
			return;
		}
	//	HttpServletResponse response = new HttpServletResponse();
	//	response.setHeader("Location", "https://localhost:8082/");
	//	response.setStatus(302);
	//	return new ModelAndView("redirect:" + "https://localhost:8082/");
		RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
		try {
			redirectStrategy.sendRedirect(arg0, arg1, "https://localhost:8082/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
