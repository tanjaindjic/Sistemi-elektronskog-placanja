package com.ftn.paymentGateway.paymentStrategy.impl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ftn.paymentGateway.dto.BankRequestDTO;
import com.ftn.paymentGateway.dto.BankResponseDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
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
	public TransakcijaStatus doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) {
		
		if(transakcija == null || podrzanoPlacanje == null) {
			return TransakcijaStatus.N;
		}
		
		BankRequestDTO theBankReq = new BankRequestDTO(podrzanoPlacanje.getIdNaloga(), podrzanoPlacanje.getIdNaloga(), transakcija.getIznos(),
				transakcija.getId(), transakcija.getVreme(), successURL, failedURL, errorURL);
		
		RestTemplate restTemplate = new RestTemplate();
		HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
		
	    ResponseEntity<BankResponseDTO> response = null;
		try {
			response = restTemplate.postForEntity(new URI(bankRequestUrl1), theBankReq, BankResponseDTO.class);
		} catch (RestClientException | URISyntaxException e) {
			e.printStackTrace();
			return TransakcijaStatus.N;
		}
		
		BankResponseDTO retVal = (BankResponseDTO) response.getBody();
	    System.out.println("*** Stiglo iz Banke ***");
	    System.out.println(retVal.toString());
	    
	    /* Dalja obrada odgovora... */
	
		return retVal.getStatus();
	}

}
