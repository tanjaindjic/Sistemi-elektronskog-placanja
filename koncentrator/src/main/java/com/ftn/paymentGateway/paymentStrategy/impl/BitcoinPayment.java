package com.ftn.paymentGateway.paymentStrategy.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;


@Component
public class BitcoinPayment implements PaymentStrategy{
	
	@Override
	public TransakcijaIshodDTO doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) throws PaymentErrorException{
		
		if(transakcija == null || podrzanoPlacanje == null) {
			throw new PaymentErrorException();
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/x-www-form-urlencoded");
		headers.set("Authorization", "Token "+podrzanoPlacanje.getIdNaloga());
		
		MultiValueMap<String, String> bitcoinRequestParams= new LinkedMultiValueMap<String, String>();
		bitcoinRequestParams.add("order_id", transakcija.getId().toString());
		bitcoinRequestParams.add("price_amount", ""+transakcija.getIznos());
		bitcoinRequestParams.add("price_currency", "USD");
		bitcoinRequestParams.add("receive_currency", "USD");
		bitcoinRequestParams.add("title", transakcija.getJedinstveniToken());

		HttpEntity<MultiValueMap<String, String>> bitcoinRequest = new HttpEntity<MultiValueMap<String, String>>(bitcoinRequestParams, headers);
		
		RestTemplate restTemplate = new RestTemplate();

	    ResponseEntity<String> bitcoinResponse = null;
	    try {
			bitcoinResponse = restTemplate.postForEntity(new URI("https://api-sandbox.coingate.com/v2/orders"), bitcoinRequest, String.class);
		} catch (RestClientException | URISyntaxException e) {
			e.printStackTrace();
			return new TransakcijaIshodDTO(false, false, TransakcijaStatus.N, null, null);
		}
		
	    JsonParser basicJsonParser = new BasicJsonParser();
	    Map<String, Object> retValMap = basicJsonParser.parseMap(bitcoinResponse.getBody()); 
		String paymentUrl = (String) retValMap.get("payment_url");
		Long bitcoinTransactionId = Long.parseLong(retValMap.get("id").toString());
		
		return new TransakcijaIshodDTO(true, true, TransakcijaStatus.C, bitcoinTransactionId, paymentUrl);
	}
	
	//Sinhronizuj bazu na svakih 10 min
	@Scheduled(initialDelay = 5000, fixedRate = 300000)
	public void syncDB() {
		
		System.out.println("Pokrenuo");
		
	}

}
