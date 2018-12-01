package com.ftn.paymentGateway.paymentStrategy.impl;

import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;

public class CreditCardPayment implements PaymentStrategy{

	@Override
	public boolean doPayment() {
		System.out.println("Doing CREDIT CARD payment \n");
		return true;
	}

}
