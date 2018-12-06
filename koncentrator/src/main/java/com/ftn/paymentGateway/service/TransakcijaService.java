package com.ftn.paymentGateway.service;

import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.Transakcija;

public interface TransakcijaService {
	
	public Transakcija getById(Long id);
	
	public Transakcija insertNewTransaction(EntitetPlacanja entitetPlacanja, PaymentRequestDTO paymentInfo);
	
}
