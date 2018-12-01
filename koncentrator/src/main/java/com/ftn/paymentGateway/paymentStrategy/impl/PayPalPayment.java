package com.ftn.paymentGateway.paymentStrategy.impl;

import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;

public class PayPalPayment implements PaymentStrategy{

	@Override
	public boolean doPayment() {
		System.out.println("Doing PAYPAL payment \n");
		return false;
	}

}
