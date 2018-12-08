package com.ftn.paymentGateway.paymentStrategy.impl;

import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;

public class PayPalPayment implements PaymentStrategy{

	@Override
	public TransakcijaStatus doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) {
		System.out.println("Doing PAYPAL payment \n");
		return TransakcijaStatus.C;
	}

}
