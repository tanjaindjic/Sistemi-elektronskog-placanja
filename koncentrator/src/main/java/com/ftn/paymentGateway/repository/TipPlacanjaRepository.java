package com.ftn.paymentGateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ftn.paymentGateway.model.TipPlacanja;

public interface TipPlacanjaRepository extends JpaRepository<TipPlacanja, Long>{

	public TipPlacanja findByKod(String kod);
	
}
