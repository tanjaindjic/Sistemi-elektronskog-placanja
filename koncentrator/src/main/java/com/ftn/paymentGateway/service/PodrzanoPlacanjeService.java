package com.ftn.paymentGateway.service;

import java.util.ArrayList;

import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;

public interface PodrzanoPlacanjeService {

	public PodrzanoPlacanje insert(PodrzanoPlacanje podrzanoPlacanje);
	
	public PodrzanoPlacanje getById(Long id);
	
	public ArrayList<PodrzanoPlacanje> getByEntitetPlacanjaAndTipPlacanja(EntitetPlacanja entitetPlacanja, TipPlacanja tipPlacanja);
	
	public ArrayList<PodrzanoPlacanje> getByEntitetPlacanja(EntitetPlacanja entitetPlacanja);
	
	public ArrayList<PodrzanoPlacanje> getByEntitetPlacanjaAndPretplata(EntitetPlacanja entitetPlacanja, boolean pretplata);
}
