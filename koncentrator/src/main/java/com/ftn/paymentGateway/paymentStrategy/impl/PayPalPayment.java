package com.ftn.paymentGateway.paymentStrategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;
import com.ftn.paymentGateway.repository.PodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.service.TransakcijaService;
import com.ftn.paymentGateway.utils.URLUtils;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

@Component
public class PayPalPayment implements PaymentStrategy{

	public static final String PAYPAL_SUCCESS_URL = "rest/success";
	public static final String PAYPAL_CANCEL_URL = "rest/cancel";
//	private GenericApplicationContext ctx;
	@Autowired
	private PodrzanoPlacanjeRepository podrzanoPlacanjeRepository;
	
	@Override
	public TransakcijaIshodDTO doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) throws PaymentErrorException{
		TransakcijaIshodDTO response = new TransakcijaIshodDTO();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
		String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
		System.out.println("CANCEL: "+ cancelUrl);
		System.out.println("SUCCESS: "+ successUrl);
	    Amount amount = new Amount();
	    amount.setCurrency("USD");
	    amount.setTotal(Double.toString(transakcija.getIznos()));
	    Transaction transaction = new Transaction();
	    transaction.setAmount(amount);
	    List<Transaction> transactions = new ArrayList<Transaction>();
	    transactions.add(transaction);

	    Payer payer = new Payer();
	    payer.setPaymentMethod("paypal");

	    Payment payment = new Payment();
	    payment.setIntent("sale");
	    payment.setPayer(payer);
	    payment.setTransactions(transactions);

	    RedirectUrls redirectUrls = new RedirectUrls();
	    redirectUrls.setCancelUrl(cancelUrl);
	    redirectUrls.setReturnUrl(successUrl);
	    payment.setRedirectUrls(redirectUrls);
	    Payment createdPayment;
	    try {
	        String redirectUrl = "";
	        
	        Map<String, String> sdkConfig = new HashMap<String, String>();
	        sdkConfig.put("mode", "sandbox");
	        String accessToken = new OAuthTokenCredential(podrzanoPlacanje.getIdNaloga(), podrzanoPlacanje.getSifraNaloga(), sdkConfig).getAccessToken();
	        APIContext apiContext = new APIContext(accessToken);
	        System.out.println(accessToken);
	                // still need to toss in the sdkConfig here
	        apiContext.setConfigurationMap(sdkConfig); 
	        createdPayment = payment.create(apiContext);
	        if(createdPayment!=null){
	            List<Links> links = createdPayment.getLinks();
	            for (Links link:links) {
	                if(link.getRel().equals("approval_url")){
	                    redirectUrl = link.getHref();
	                    response.setNovaPutanja(link.getHref());
	                    response.setIzvrsnaTransakcija(createdPayment.getId());
	                    System.out.println("ID IZVRSNE TRANSAKCIJE: "+createdPayment.getId());
	                    break;
	                }
	            }
	            response.setRedirekcija(true);
	            response.setNoviStatus(TransakcijaStatus.C);
	            response.setUspesno(true);
	        }
	    } catch (PayPalRESTException e) {
	        System.out.println("Error happened during payment creation!");
	        response.setUspesno(false);
	        response.setNoviStatus(TransakcijaStatus.N);
	    }
	    return response;
	}


	public Boolean completePayment(HttpServletRequest request, PodrzanoPlacanje podrzanoPlacanje) {
		String response = "";
	    Payment payment = new Payment();
	    payment.setId(request.getParameter("paymentId"));

	    PaymentExecution paymentExecution = new PaymentExecution();
	    paymentExecution.setPayerId(request.getParameter("PayerID"));
	    try {
	    	Map<String, String> sdkConfig = new HashMap<String, String>();
	        sdkConfig.put("mode", "sandbox");
	        String accessToken = new OAuthTokenCredential(podrzanoPlacanje.getIdNaloga(), podrzanoPlacanje.getSifraNaloga(), sdkConfig).getAccessToken();
	        APIContext apiContext = new APIContext(accessToken);
	        System.out.println(accessToken);
	        apiContext.setConfigurationMap(sdkConfig); 
	        Payment createdPayment = payment.execute(apiContext, paymentExecution);
	        if(createdPayment!=null){
	            return true;
	        }
	    } catch (PayPalRESTException e) {
	        System.err.println(e.getDetails());
	    }
	    return false;
	}
		
		
		
		/*	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
		String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
		TransakcijaIshodDTO retVal = new TransakcijaIshodDTO();
		try {
			Payment payment = createPayment(
					transakcija.getIznos(), 
					"USD", 
					PaypalPaymentMethod.paypal, 
					PaypalPaymentIntent.sale,
					"payment description", 
					cancelUrl, 
					successUrl,
					podrzanoPlacanje);
			for(Links links : payment.getLinks()){
				if(links.getRel().equals("approval_url")){
					System.out.println("PUTANJA JE:" + links.getHref());
					retVal.setNovaPutanja(links.getHref());
					retVal.setRedirekcija(true);
					retVal.setNoviStatus(TransakcijaStatus.C);
					retVal.setUspesno(true);
				}
			}
		} catch (PayPalRESTException e) {
			System.out.println(e.getMessage());
			retVal.setUspesno(false);
			retVal.setNoviStatus(TransakcijaStatus.N);
		}	
		
		return retVal;
	}

	
	public Payment executePayment(String paymentId, String payerId, PodrzanoPlacanje podrzanoPlacanje)  throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		APIContext apiContext = new APIContext(authTokenCredential(podrzanoPlacanje).getAccessToken());
		return payment.execute(apiContext, paymentExecute);
	}

	public Payment createPayment(
			Double total, 
			String currency, 
			PaypalPaymentMethod method, 
			PaypalPaymentIntent intent,
			String description, 
			String cancelUrl, 
			String successUrl,
			PodrzanoPlacanje podrzanoPlacanje) throws PayPalRESTException{
		Amount amount = new Amount();
		amount.setCurrency(currency);
		total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
		amount.setTotal(String.format("%.2f", total));

		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);

		Payer payer = new Payer();
		payer.setPaymentMethod(method.toString());

		Payment payment = new Payment();
		payment.setIntent(intent.toString());
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);
		APIContext apiContext = new APIContext(authTokenCredential(podrzanoPlacanje).getAccessToken());
		
		return payment.create(apiContext);
	}
	public Map<String, String> paypalSdkConfig(){
		Map<String, String> sdkConfig = new HashMap<>();
		sdkConfig.put("mode", "sandbox");
		return sdkConfig;
	}
	
	public OAuthTokenCredential authTokenCredential(PodrzanoPlacanje podrzanoPlacanje){
		return new OAuthTokenCredential(podrzanoPlacanje.getIdNaloga(), podrzanoPlacanje.getSifraNaloga(), paypalSdkConfig());
	}*/
}
