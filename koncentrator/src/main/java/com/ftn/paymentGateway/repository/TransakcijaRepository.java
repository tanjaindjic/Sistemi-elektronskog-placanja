package com.ftn.paymentGateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ftn.paymentGateway.model.Transakcija;

public interface TransakcijaRepository extends JpaRepository<Transakcija, Long>{

	public Transakcija findByJedinstveniToken(String jedinstveniToken);

	public Transakcija findByIzvrsnaTransakcija(Long decode);
	
}
