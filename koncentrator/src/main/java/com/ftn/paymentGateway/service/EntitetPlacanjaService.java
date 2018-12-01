package com.ftn.paymentGateway.service;

import com.ftn.paymentGateway.model.EntitetPlacanja;


public interface EntitetPlacanjaService {
	
	public EntitetPlacanja insert(EntitetPlacanja entitetPlacanja);
	
	public EntitetPlacanja getById(Long id);
	

}
