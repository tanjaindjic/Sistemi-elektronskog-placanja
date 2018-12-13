package com.ftn.paymentGateway.exceptions;

public class PaymentErrorException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public PaymentErrorException() {
		super("Greska prilikom obavljanja placanja.");
	}

}
