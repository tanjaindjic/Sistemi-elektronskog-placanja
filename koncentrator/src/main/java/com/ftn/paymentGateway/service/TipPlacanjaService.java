package com.ftn.paymentGateway.service;

import java.util.List;

import com.ftn.paymentGateway.model.TipPlacanja;

public interface TipPlacanjaService {

	public TipPlacanja insert(TipPlacanja tipPlacanja);
	
	public TipPlacanja getById(Long id);
	
	public TipPlacanja getByKod(String kod);
	
	public List<TipPlacanja> getAll();
	
}
