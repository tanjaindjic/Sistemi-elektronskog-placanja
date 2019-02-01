package com.ftn.paymentGateway.service;

import com.ftn.paymentGateway.dto.EntitetPlacanjaDTO;
import com.ftn.paymentGateway.model.EntitetPlacanja;


public interface EntitetPlacanjaService {
	
	public EntitetPlacanja insert(EntitetPlacanja entitetPlacanja);
	
	public EntitetPlacanja getById(Long id);
	
	public EntitetPlacanja getByIdentifikacioniKod(String identifikacioniKod);
	
	public boolean validateChain(EntitetPlacanjaDTO entitetPlacanja);

	public String getUrlLocation(EntitetPlacanja entitetPlacanja);

	public String getUrlResponse(EntitetPlacanja entitetPlacanja);

}
