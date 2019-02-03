package com.ftn.paymentGateway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;

public interface TransakcijaRepository extends JpaRepository<Transakcija, Long>{

	public Transakcija findByJedinstveniToken(String jedinstveniToken);

	public Transakcija findByIzvrsnaTransakcija(String izvrsnaTransakcija);
	
	@Query("from Transakcija as t where t.jedinstveniToken = ?1 and t.status = 'C' and (t.vreme >= ?2 and t.vreme <= ?3)")
	public Transakcija checkTokenValidity(String token, Date startTimestamp, Date endTimestamp);
	
	public List<Transakcija> findFirst10ByStatusAndTipPlacanja(TransakcijaStatus status, TipPlacanja tipPlacanja);

	public List<Transakcija> findByStatusAndTipPlacanja(TransakcijaStatus c, TipPlacanja payPalTip);
	
}
