package com.ftn.paymentGateway.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
