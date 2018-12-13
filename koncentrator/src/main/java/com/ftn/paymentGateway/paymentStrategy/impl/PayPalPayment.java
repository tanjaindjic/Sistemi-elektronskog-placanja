package com.ftn.paymentGateway.paymentStrategy.impl;

import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;

public class PayPalPayment implements PaymentStrategy{

	@Override
	public TransakcijaIshodDTO doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) throws PaymentErrorException{
		System.out.println("Doing PAYPAL payment \n");
		return new TransakcijaIshodDTO();
	}

}
