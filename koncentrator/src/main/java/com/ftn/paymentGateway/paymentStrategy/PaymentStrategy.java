package com.ftn.paymentGateway.paymentStrategy;

import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.Transakcija;

public interface PaymentStrategy {

	public TransakcijaStatus doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje);
	
}
