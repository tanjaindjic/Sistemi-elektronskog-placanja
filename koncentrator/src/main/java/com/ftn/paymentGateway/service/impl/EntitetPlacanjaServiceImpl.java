package com.ftn.paymentGateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.dto.EntitetPlacanjaDTO;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.repository.EntitetPlacanjaRepository;
import com.ftn.paymentGateway.repository.PodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.repository.TipPlacanjaRepository;
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

	@Override
	public EntitetPlacanja getByIdentifikacioniKod(String identifikacioniKod) {
		
		return entitetPlacanjaRepository.findByIdentifikacioniKod(identifikacioniKod);
	}

	@Override
	public boolean validateChain(EntitetPlacanjaDTO entitetPlacanja) {
		
		EntitetPlacanja currentLeaf = entitetPlacanjaRepository.findByIdentifikacioniKod(entitetPlacanja.getIdentifikacioniKod());
		
		if(currentLeaf == null) {
			return false;
		}
		
		if(!currentLeaf.isPoslovniSaradnik()) {
			EntitetPlacanja newLeaf = currentLeaf.getNadredjeni();
			
			if(newLeaf == null) {
				return false;
			}
			return validateChain(entitetPlacanja.getNadredjeni());
		}
		
		return true;
	}
	
	
	
	
	
}
