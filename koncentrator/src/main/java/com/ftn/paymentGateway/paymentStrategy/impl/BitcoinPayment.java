package com.ftn.paymentGateway.paymentStrategy.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.IdPoljePlacanja;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.exceptions.TransactionUpdateExeption;
import com.ftn.paymentGateway.exceptions.UnsupportedMethodException;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.PoljePodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;
import com.ftn.paymentGateway.repository.PodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.repository.TipPlacanjaRepository;
import com.ftn.paymentGateway.repository.TransakcijaRepository;


@Service
public class BitcoinPayment implements PaymentStrategy{
	
	@Autowired
	private TransakcijaRepository transakcijaRepository;
	
	@Autowired
	private TipPlacanjaRepository tipPlacanjaRepository;
	
	@Autowired
	private PodrzanoPlacanjeRepository podrzanoPlacanjeRepository;
	
	@Override
	public TransakcijaIshodDTO doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) throws PaymentErrorException{
		
		if(transakcija == null || podrzanoPlacanje == null) {
			throw new PaymentErrorException();
		}
		
		String idNaloga = getIdNaloga(podrzanoPlacanje.getPolja());
		
		if(idNaloga == null) {
			return new TransakcijaIshodDTO(false, false, TransakcijaStatus.N, null, null);
		}
		
		if(idNaloga.isEmpty()) {
			return new TransakcijaIshodDTO(false, false, TransakcijaStatus.N, null, null);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/x-www-form-urlencoded");
		headers.set("Authorization", "Token "+idNaloga);
		
		MultiValueMap<String, String> bitcoinRequestParams= new LinkedMultiValueMap<String, String>();
		bitcoinRequestParams.add("order_id", transakcija.getId().toString());
		bitcoinRequestParams.add("price_amount", ""+transakcija.getIznos());
		bitcoinRequestParams.add("price_currency", "USD");
		bitcoinRequestParams.add("receive_currency", "USD");
		bitcoinRequestParams.add("success_url", transakcija.getSuccessURL());
		bitcoinRequestParams.add("cancel-url", transakcija.getErrorURL());
		bitcoinRequestParams.add("title", transakcija.getJedinstveniToken());

		HttpEntity<MultiValueMap<String, String>> bitcoinRequest = new HttpEntity<MultiValueMap<String, String>>(bitcoinRequestParams, headers);
		
		RestTemplate restTemplate = new RestTemplate();

	    ResponseEntity<String> bitcoinResponse = null;
	    try {
			bitcoinResponse = restTemplate.postForEntity(new URI("https://api-sandbox.coingate.com/v2/orders"), bitcoinRequest, String.class);
		} catch (RestClientException | URISyntaxException e) {
			System.out.println("Greska prilikom slanja zahteva, BITCOIN.");
			return new TransakcijaIshodDTO(false, false, TransakcijaStatus.N, null, null);
		}
		
	    JsonParser basicJsonParser = new BasicJsonParser();
	    Map<String, Object> retValMap = basicJsonParser.parseMap(bitcoinResponse.getBody()); 
	    
		String paymentUrl = (String) retValMap.get("payment_url");
		Long bitcoinTransactionId = Long.parseLong(retValMap.get("id").toString());
		
		return new TransakcijaIshodDTO(true, true, TransakcijaStatus.C, Long.toString(bitcoinTransactionId), paymentUrl);
	}
	
	@Override
	@Scheduled(initialDelay = 10000, fixedRate = 1200000) // Odlozi 10s, ponavljaj na 20min
	public void syncDB() {
		
		List<Transakcija> pending = null;
		TipPlacanja bitcoinPaymentType = tipPlacanjaRepository.findByKod("BCP");
		
		try {
			pending = transakcijaRepository.findFirst10ByStatusAndTipPlacanja(TransakcijaStatus.C, bitcoinPaymentType);
		}catch(Exception e) {
			return;
		}
		
		RestTemplate restTemplate = new RestTemplate();
			
		for(Transakcija temp : pending) {
			
			PodrzanoPlacanje accountInfo = podrzanoPlacanjeRepository.findByEntitetPlacanjaAndTipPlacanja(temp.getEntitetPlacanja(), bitcoinPaymentType).get(0);
			
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Token "+getIdNaloga(accountInfo.getPolja()));
			
			HttpEntity<?> bitcoinRequest = new HttpEntity<>(headers);
		    ResponseEntity<String> bitcoinResponse = null;
		    
		    try {
				bitcoinResponse = restTemplate.exchange("https://api-sandbox.coingate.com/v2/orders/"+temp.getIzvrsnaTransakcija(), HttpMethod.GET, bitcoinRequest, String.class);
			} catch (RestClientException e) {
				System.out.println("Greska prilikom sinhronizacije zahteva, BITCOIN.");
				//e.printStackTrace();
				break;
			}
		    
		    JsonParser basicJsonParser = new BasicJsonParser();
		    Map<String, Object> paymentInfo = basicJsonParser.parseMap(bitcoinResponse.getBody());
		    
		    try {
				changeStatus(temp, bindStatus(paymentInfo.get("status").toString()));
			} catch (TransactionUpdateExeption e) {
				System.out.println("Greska prilikom promene statusa transakcije, BITCOIN.");
				//e.printStackTrace();
				break;
			}
		    
		}
	}

	@Override
	public Boolean completePayment(HttpServletRequest request, PodrzanoPlacanje podrzanoPlacanje) throws UnsupportedMethodException {

		throw new UnsupportedMethodException();
	}
	
	private String getIdNaloga(List<PoljePodrzanoPlacanje> polja) {
		String retVal = "";
		
		for(PoljePodrzanoPlacanje polje : polja) {
			if(polje.getIdPolja().equals(IdPoljePlacanja.MERCHANT_ID)) {
				retVal = polje.getVrednost();
				break;
			}	
		}
		
		return retVal;
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	private void changeStatus(Transakcija transakcija, TransakcijaStatus newStatus) throws TransactionUpdateExeption {
		
		if(transakcija == null || newStatus == null) {
			throw new TransactionUpdateExeption();
		}
		
		if(transakcija.getStatus() != TransakcijaStatus.C || newStatus == TransakcijaStatus.E || newStatus == TransakcijaStatus.C){
			throw new TransactionUpdateExeption();
		}
		
		transakcija.setStatus(newStatus);
		transakcija = transakcijaRepository.save(transakcija);
		
		System.out.println("USPESNO! "+transakcija.getIzvrsnaTransakcija()+" "+transakcija.getStatus());
	}
	
	private TransakcijaStatus bindStatus(String statusCode) {
		
		switch (statusCode) {
			case "new" :
	        	return TransakcijaStatus.C;
			case "pending" :
	        	return TransakcijaStatus.C;
	        case "paid":
	        	return TransakcijaStatus.U;
	        case "invalid" :
	        	return TransakcijaStatus.N;
	        case "canceled" :
	        	return TransakcijaStatus.N;
	        case "expired" :
	        	return TransakcijaStatus.E;
		}

		return null;
	}

}
