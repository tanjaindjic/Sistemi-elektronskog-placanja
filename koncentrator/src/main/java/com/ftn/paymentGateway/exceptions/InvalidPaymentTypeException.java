package com.ftn.paymentGateway.exceptions;

public class InvalidPaymentTypeException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public InvalidPaymentTypeException() {
		super("Neispravan nacin placanja.");
	}

}
