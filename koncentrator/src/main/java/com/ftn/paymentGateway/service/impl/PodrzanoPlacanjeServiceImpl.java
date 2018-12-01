package com.ftn.paymentGateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.repository.PodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.service.PodrzanoPlacanjeService;

@Service
public class PodrzanoPlacanjeServiceImpl implements PodrzanoPlacanjeService{

	@Autowired 
	private PodrzanoPlacanjeRepository podrzanoPlacanjeRepository;

	@Override
	public PodrzanoPlacanje insert(PodrzanoPlacanje podrzanoPlacanje) {
		
		return podrzanoPlacanjeRepository.save(podrzanoPlacanje);
	}

	@Override
	public PodrzanoPlacanje getById(Long id) {
		
		return podrzanoPlacanjeRepository.getOne(id);
	}
	
}
