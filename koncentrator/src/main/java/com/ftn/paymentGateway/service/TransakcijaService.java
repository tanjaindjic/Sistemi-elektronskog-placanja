package com.ftn.paymentGateway.service;

import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.exceptions.TransactionUpdateExeption;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.Transakcija;

public interface TransakcijaService {
	
	public Transakcija getById(Long id);
	
	public Transakcija insertNewTransaction(EntitetPlacanja entitetPlacanja, PaymentRequestDTO paymentInfo);
	
	public Transakcija getByJedinstveniToken(String jedinstveniToken);
	
	public Transakcija update(TransakcijaIshodDTO transakcijaIshod, Transakcija transakcija) throws TransactionUpdateExeption;

	public Transakcija findByIzvrsnaTransakcija(Long decode);
	
}
