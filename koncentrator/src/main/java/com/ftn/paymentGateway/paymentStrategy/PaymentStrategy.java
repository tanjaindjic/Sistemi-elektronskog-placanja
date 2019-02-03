package com.ftn.paymentGateway.paymentStrategy;

import javax.servlet.http.HttpServletRequest;

import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.exceptions.UnsupportedMethodException;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.Transakcija;

public interface PaymentStrategy {

	public TransakcijaIshodDTO doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) throws PaymentErrorException;
	
	public Boolean completePayment (HttpServletRequest request, PodrzanoPlacanje podrzanoPlacanje) throws UnsupportedMethodException;

	public void syncDB();
}
