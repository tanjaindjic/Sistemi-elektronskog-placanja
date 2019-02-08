package com.ftn.paymentGateway.paymentStrategy.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ftn.paymentGateway.dto.BankRequestDTO;
import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.IdPoljePlacanja;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.exceptions.UnsupportedMethodException;
import com.ftn.paymentGateway.helpClasses.RSAEncryptDecrypt;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.PoljePodrzanoPlacanje;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;
import com.ftn.paymentGateway.utils.URLUtils;

@Service
public class CreditCardPayment implements PaymentStrategy{

	private String errorURL = "rest/success";

	private String bankRequestUrl1 = "https://localhost:8081/";

	private String bankRequestUrl2 = "https://localhost:8082/";


	


	@SuppressWarnings("unchecked")
	@Override
	public TransakcijaIshodDTO doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) throws PaymentErrorException{

		if(transakcija == null || podrzanoPlacanje == null) {
			return new TransakcijaIshodDTO(false, false, TransakcijaStatus.N, null, null);
		}
		
		String merchant_id = "";
	    String merchant_secret = "";
		
		for(PoljePodrzanoPlacanje polje : podrzanoPlacanje.getPolja()) {
			if(polje.getIdPolja().equals(IdPoljePlacanja.MERCHANT_ID)) {
				merchant_id = polje.getVrednost();
			}
			else if(polje.getIdPolja().equals(IdPoljePlacanja.MERCHANT_PASSWORD)){
				merchant_secret = polje.getVrednost();
			}
		}
		System.out.println("ENCRYPT MERCHANT ID........................"+merchant_id);
		System.out.println("ENCRYPT MERCHANT SECRET...................."+merchant_secret);
		try {
			merchant_id = RSAEncryptDecrypt.decrypt(merchant_id);
			System.out.println("DECRYPTED MERCHANT ID........................"+merchant_id);
			merchant_secret = RSAEncryptDecrypt.decrypt(merchant_secret);
			System.out.println("DECRYPTED MERCHANT SECRET...................."+merchant_secret);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("greska prilikom dekriptovanja - NEMOGUC PRISTUP BITNIM KREDENCIJALIMA");
			e1.printStackTrace();
			return null;
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		//String urlBase = URLUtils.getBaseURl(request) + "/";
		BankRequestDTO theBankReq = new BankRequestDTO(merchant_id, merchant_secret, transakcija.getIznos(),
				transakcija.getId(), transakcija.getVreme(), transakcija.getSuccessURL(), transakcija.getFailedURL(), transakcija.getErrorURL());
		
		RestTemplate restTemplate = new RestTemplate();
		HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
		
	    ResponseEntity<HashMap> response = null;
		try {
			System.out.println(bankRequestUrl1+"initiatePayment");
			response = restTemplate.postForEntity(new URI(bankRequestUrl1+"initiatePayment"), theBankReq, HashMap.class);
		} catch (RestClientException | URISyntaxException e) {
			e.printStackTrace();
			return new TransakcijaIshodDTO(false, true, TransakcijaStatus.C, null, "errorUrl");
		}
		
		Map<String, Object> retVal = (Map<String, Object>) response.getBody();
	
		return new TransakcijaIshodDTO(true, true, TransakcijaStatus.C, retVal.get("izvrsnaTransakcijaId").toString(), retVal.get("paymentURL").toString());
	}

	@Override
	public Boolean completePayment(HttpServletRequest request, PodrzanoPlacanje podrzanoPlacanje)
			throws UnsupportedMethodException {
		
		return true;
	}

	@Override
	@Scheduled(initialDelay = 5000, fixedRate = 300000)
	public void syncDB() {
		
	}

}
