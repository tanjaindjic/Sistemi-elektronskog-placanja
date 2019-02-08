package com.ftn.paymentGateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ftn.paymentGateway.model.EntitetPlacanja;

public interface EntitetPlacanjaRepository extends JpaRepository<EntitetPlacanja, Long>{

	public EntitetPlacanja findByIdentifikacioniKod(String identifikacioniKod);
	
	public List<EntitetPlacanja> findByPoslovniSaradnik(boolean poslovniSaradnik);
	
	public List<EntitetPlacanja> findByNadredjeni(EntitetPlacanja nadredjeni);
	
}
