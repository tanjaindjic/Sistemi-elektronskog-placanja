package com.ftn.paymentGateway.paymentStrategy;

import org.springframework.beans.factory.annotation.Autowired;

import com.ftn.paymentGateway.exceptions.InvalidPaymentTypeException;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.repository.TipPlacanjaRepository;

public class PaymentFactory {
	
	@Autowired 
	private TipPlacanjaRepository tipPlacanjaRepository;
	
	public PaymentFactory() {
		super();
	}

	public PaymentStrategy getPaymentStrategy(String paymentType) throws InvalidPaymentTypeException{
		
		TipPlacanja tp = tipPlacanjaRepository.findByKod(paymentType);
		
		if(tp == null) {
			throw new InvalidPaymentTypeException();
		}
		
		try {
			
			return (PaymentStrategy) Class.forName("com.ftn.paymentGateway.paymentStrategy.impl."+tp.getKlasa()).newInstance(); 
		}catch(Exception e) {
			throw new InvalidPaymentTypeException();
		}
		
	}
}
