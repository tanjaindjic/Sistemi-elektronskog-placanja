package com.ftn.paymentGateway.paymentStrategy.impl;

import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;

public class BitcoinPayment implements PaymentStrategy{
	
	@Override
	public boolean doPayment() {
		System.out.println("Doing BITCOIN payment \n");
		return true;
	}

}
