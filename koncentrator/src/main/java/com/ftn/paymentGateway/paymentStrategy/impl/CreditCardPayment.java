package com.ftn.paymentGateway.paymentStrategy.impl;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.ftn.paymentGateway.dto.BankRequestDTO;
import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.exceptions.UnsupportedMethodException;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;

public class CreditCardPayment implements PaymentStrategy{
	
	@Value("${frontend.successURL}")
	private String successURL;
	
	@Value("${frontend.failedURL}")
	private String failedURL;
	
	@Value("${frontend.errorURL}")
	private String errorURL;
	
	@Value("${bank.requestUrl1}")
	private String bankRequestUrl1;
	
	@Value("${bank.requestUrl2}")
	private String bankRequestUrl2;

	@Override
	public TransakcijaIshodDTO doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) throws PaymentErrorException{
		
		if(transakcija == null || podrzanoPlacanje == null) {
			return new TransakcijaIshodDTO(false, false, TransakcijaStatus.N, null, null);
		}
		
		BankRequestDTO theBankReq = new BankRequestDTO(podrzanoPlacanje.getIdNaloga(), podrzanoPlacanje.getIdNaloga(), transakcija.getIznos(),
				transakcija.getId(), transakcija.getVreme(), successURL, failedURL, errorURL);
		
		RestTemplate restTemplate = new RestTemplate();
		HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
		
		/*
	    ResponseEntity<BankResponseDTO> response = null;
		try {
			response = restTemplate.postForEntity(new URI(bankRequestUrl1), theBankReq, BankResponseDTO.class);
		} catch (RestClientException | URISyntaxException e) {
			e.printStackTrace();
			return TransakcijaStatus.N;
		}
		
		BankResponseDTO retVal = (BankResponseDTO) response.getBody();
	    */
	    
	    /* Dalja obrada odgovora... */
	
		return new TransakcijaIshodDTO();
	}

	@Override
	public Boolean completePayment(HttpServletRequest request, PodrzanoPlacanje podrzanoPlacanje)
			throws UnsupportedMethodException {
		// TODO Auto-generated method stub
		return null;
	}

}
