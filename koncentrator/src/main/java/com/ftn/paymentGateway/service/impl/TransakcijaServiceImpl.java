package com.ftn.paymentGateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.repository.TransakcijaRepository;
import com.ftn.paymentGateway.service.TransakcijaService;

@Service
public class TransakcijaServiceImpl implements TransakcijaService{
	
	@Autowired
	private TransakcijaRepository transakcijaRepository;

	@Override
	public Transakcija getById(Long id) {
		
		return transakcijaRepository.getOne(id);
	}

}
