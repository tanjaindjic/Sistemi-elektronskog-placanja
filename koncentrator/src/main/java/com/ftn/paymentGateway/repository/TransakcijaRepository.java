package com.ftn.paymentGateway.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ftn.paymentGateway.model.Transakcija;

public interface TransakcijaRepository extends JpaRepository<Transakcija, Long>{

	public Transakcija findByJedinstveniToken(String jedinstveniToken);

	public Transakcija findByIzvrsnaTransakcija(String izvrsnaTransakcija);
	
	@Query("from Transakcija as t where t.jedinstveniToken = ?1 and t.status = 'C' and (t.vreme >= ?2 and t.vreme <= ?3)")
	public Transakcija checkTokenValidity(String token, Date startTimestamp, Date endTimestamp);
	
}
