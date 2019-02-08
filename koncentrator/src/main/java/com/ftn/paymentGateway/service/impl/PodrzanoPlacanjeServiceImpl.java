package com.ftn.paymentGateway.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.helpClasses.RSAEncryptDecrypt;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.PoljePodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
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

	@Override
	public ArrayList<PodrzanoPlacanje> getByEntitetPlacanjaAndTipPlacanja(EntitetPlacanja entitetPlacanja,
			TipPlacanja tipPlacanja) {
		
		return podrzanoPlacanjeRepository.findByEntitetPlacanjaAndTipPlacanja(entitetPlacanja, tipPlacanja);
	}

	@Override
	public ArrayList<PodrzanoPlacanje> getByEntitetPlacanja(EntitetPlacanja entitetPlacanja) {
		
		return podrzanoPlacanjeRepository.findByEntitetPlacanja(entitetPlacanja);
	}

	@Override
	public ArrayList<PodrzanoPlacanje> getByEntitetPlacanjaAndPretplata(EntitetPlacanja entitetPlacanja, boolean pretplata) {
		
		return podrzanoPlacanjeRepository.findByEntitetPlacanjaAndPretplata(entitetPlacanja, pretplata);
	}
	
}
