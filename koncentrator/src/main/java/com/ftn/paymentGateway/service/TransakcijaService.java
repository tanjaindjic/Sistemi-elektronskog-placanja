package com.ftn.paymentGateway.service;

import java.util.List;

import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.TransactionUpdateExeption;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;

public interface TransakcijaService {
	
	public Transakcija getById(Long id);
	
	public Transakcija insertNewTransaction(EntitetPlacanja entitetPlacanja, PaymentRequestDTO paymentInfo, boolean pretplata, String successUrl, String failedUrl, String errorUrl);
	
	public Transakcija getByJedinstveniToken(String jedinstveniToken);
	
	public Transakcija update(TransakcijaIshodDTO transakcijaIshod, Transakcija transakcija) throws TransactionUpdateExeption;

	public Transakcija findByIzvrsnaTransakcija(String izvrsnaTransakcija);

	public Transakcija save(Transakcija transakcija);
	
	public boolean checkTokenValidity(Transakcija transakcija);
	
	public List<Transakcija> get10ByStatusAndType(TransakcijaStatus status, TipPlacanja tipPlacanja);
	
}
