package com.ftn.paymentGateway.exceptions;

public class TransactionUpdateExeption extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public TransactionUpdateExeption() {
		super("Greska prilikom izmene transakcije.");
	}
}
