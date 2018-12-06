package com.ftn.paymentGateway.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.helpClasses.RandomStringGenerator;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.repository.TransakcijaRepository;
import com.ftn.paymentGateway.service.TransakcijaService;

@Service
public class TransakcijaServiceImpl implements TransakcijaService{
	
	@Autowired
	private TransakcijaRepository transakcijaRepository;
	
	@Autowired
	private RandomStringGenerator randomStringGenerator;

	@Override
	public Transakcija getById(Long id) {
		
		return transakcijaRepository.getOne(id);
	}

	@Override
	public Transakcija insertNewTransaction(EntitetPlacanja entitetPlacanja, PaymentRequestDTO paymentInfo) {
		
		if(paymentInfo.getIznos() < 0.00 || entitetPlacanja == null) {
			return null;
		}
		
		Transakcija newPayment = new Transakcija(null, paymentInfo.getMaticnaTransakcija(), null, paymentInfo.getIznos(),
				new Date(System.currentTimeMillis()), TransakcijaStatus.C, randomStringGenerator.genRandomString(90), entitetPlacanja, null);
		
		return transakcijaRepository.save(newPayment);
	}

}
