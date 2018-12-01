package com.ftn.paymentGateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.repository.EntitetPlacanjaRepository;
import com.ftn.paymentGateway.service.EntitetPlacanjaService;

@Service
public class EntitetPlacanjaServiceImpl implements EntitetPlacanjaService {

	@Autowired
	private EntitetPlacanjaRepository entitetPlacanjaRepository;

	@Override
	public EntitetPlacanja insert(EntitetPlacanja entitetPlacanja) {
		
		return entitetPlacanjaRepository.save(entitetPlacanja);
	}

	@Override
	public EntitetPlacanja getById(Long id) {
		
		return entitetPlacanjaRepository.getOne(id);
	}
	
	
	
	
	
}
