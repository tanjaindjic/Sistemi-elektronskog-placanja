package com.ftn.paymentGateway.exceptions;

public class UnsupportedMethodException extends Exception {
private static final long serialVersionUID = 1L;
	
	public UnsupportedMethodException() {
		super("Metoda nije podrzana od strane servisa za placanje.");
	}
}
