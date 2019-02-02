package com.ftn.paymentGateway.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.repository.TipPlacanjaRepository;
import com.ftn.paymentGateway.service.TipPlacanjaService;

@Service
public class TipPlacanjaServiceImpl implements TipPlacanjaService{
	
	@Autowired
	private TipPlacanjaRepository tipPlacanjaRepository;

	@Override
	public TipPlacanja insert(TipPlacanja tipPlacanja) {
		
		return tipPlacanjaRepository.save(tipPlacanja);
	}

	@Override
	public TipPlacanja getById(Long id) {
		
		return tipPlacanjaRepository.getOne(id);
	}

	@Override
	public TipPlacanja getByKod(String kod) {
		
		System.out.println("POZVAO za KOD");
		return tipPlacanjaRepository.findByKod(kod);
	}

	@Override
	public List<TipPlacanja> getAll() {
		// TODO Auto-generated method stub
		return tipPlacanjaRepository.findAll();
	}
	
	

}
