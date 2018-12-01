package com.ftn.paymentGateway.service;

import com.ftn.paymentGateway.model.PodrzanoPlacanje;

public interface PodrzanoPlacanjeService {

	public PodrzanoPlacanje insert(PodrzanoPlacanje podrzanoPlacanje);
	
	public PodrzanoPlacanje getById(Long id);
	
}
