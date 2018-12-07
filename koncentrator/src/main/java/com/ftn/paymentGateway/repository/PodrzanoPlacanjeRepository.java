package com.ftn.paymentGateway.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;

public interface PodrzanoPlacanjeRepository extends JpaRepository<PodrzanoPlacanje, Long>{

	public ArrayList<PodrzanoPlacanje> findByEntitetPlacanjaAndTipPlacanja(EntitetPlacanja entitetPlacanja, TipPlacanja tipPlacanja);
	
	public ArrayList<PodrzanoPlacanje> findByEntitetPlacanja(EntitetPlacanja entitetPlacanja);
	
}
