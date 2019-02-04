package com.ftn.paymentGateway.paymentStrategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.IdPoljePlacanja;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.PoljePodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentStrategy;
import com.ftn.paymentGateway.repository.PodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.repository.TipPlacanjaRepository;
import com.ftn.paymentGateway.repository.TransakcijaRepository;
import com.ftn.paymentGateway.utils.URLUtils;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.PaymentHistory;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.base.rest.RESTUtil;


@Service
public class PayPalPayment implements PaymentStrategy  {

	public static final String PAYPAL_SUCCESS_URL = "rest/success";
	public static final String PAYPAL_CANCEL_URL = "rest/cancel";
	public static PaymentHistory ph = null;
//	private GenericApplicationContext ctx;
	
	
	
	@Autowired
	private PodrzanoPlacanjeRepository podrzanoPlacanjeRepository;
	@Autowired
	private TipPlacanjaRepository tipPlacanjaRepository;
	@Autowired
	private TransakcijaRepository transakcijaRepository;
	
	
	
	public PayPalPayment() {
	}
	@Override
	public TransakcijaIshodDTO doPayment(Transakcija transakcija, PodrzanoPlacanje podrzanoPlacanje) throws PaymentErrorException{
		if(transakcija==null || podrzanoPlacanje==null){
			throw new PaymentErrorException();
		}
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
	    try {
	        String redirectUrl = "";
	        
	        Map<String, String> sdkConfig = new HashMap<String, String>();
	        sdkConfig.put("mode", "sandbox");
	        String accessToken = new OAuthTokenCredential(merchant_id, merchant_secret, sdkConfig).getAccessToken();
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
	    try {
	    	Map<String, String> sdkConfig = new HashMap<String, String>();
	        sdkConfig.put("mode", "sandbox");
	        String accessToken = new OAuthTokenCredential(merchant_id, merchant_secret, sdkConfig).getAccessToken();
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


	@Override
	@Scheduled(initialDelay = 5000, fixedRate = 30000)
	public void syncDB() {
		List<Transakcija> ppTransakcije = null;
		TipPlacanja payPalTip = tipPlacanjaRepository.findByKod("PPP");
		try {
			ppTransakcije = transakcijaRepository.findByStatusAndTipPlacanja(TransakcijaStatus.C, payPalTip);
		}catch(Exception e) {
			return;
		}		
			
		PodrzanoPlacanje accountInfo =  podrzanoPlacanjeRepository.findDistinctByTipPlacanja(payPalTip).get(0);
		if(accountInfo==null){
			System.out.println("NINA KRALJU");
			return;
		}
		
		HttpHeaders headers = new HttpHeaders();
		String merchant_id = "";
		for(PoljePodrzanoPlacanje polje : accountInfo.getPolja()) {
			if(polje.getIdPolja().equals(IdPoljePlacanja.MERCHANT_ID)) {
				merchant_id = polje.getVrednost();
				break;
			}	
		}
		String merchant_secret = "";
		for(PoljePodrzanoPlacanje polje : accountInfo.getPolja()) {
			if(polje.getIdPolja().equals(IdPoljePlacanja.MERCHANT_PASSWORD)) {
				merchant_secret = polje.getVrednost();
				break;
			}	
		}
		
		//////////
		Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", "sandbox");
        String accessToken = "";
        HashMap<String, String> statusi = new HashMap<String, String>();
		try {
			accessToken = new OAuthTokenCredential(merchant_id, merchant_secret, sdkConfig).getAccessToken();
			statusi = getStatus(accessToken);
		} catch (PayPalRESTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("NE moze da dobije accessToken");
			return;
		}
		////////
		if(statusi==null){
			return;	
		}
//        System.out.println("*******ACCESS TOKEN******    "+accessToken);
		for(Transakcija ppt0 : ppTransakcije) {
			System.out.println("-----"+ppt0.getIzvrsnaTransakcija());
			if(!ppt0.getStatus().equals(TransakcijaStatus.C))
				continue;
			String status = statusi.get(ppt0.getIzvrsnaTransakcija());
			if(status==null)
				continue;
					System.out.println(statusi.get(ppt0.getIzvrsnaTransakcija()));
					System.out.println(".........................................");
			//opcije za status su failed, created i approved
			if(status.equals("approved")){
				ppt0.setStatus(TransakcijaStatus.U);
				transakcijaRepository.save(ppt0);
			}
			else if(status.equals("failed")){
				ppt0.setStatus(TransakcijaStatus.E);
				transakcijaRepository.save(ppt0);
			}
			
		}
	}


	private HashMap<String, String> getStatus(String accessToken) throws PayPalRESTException {
		HashMap<String, String> mapa = new HashMap<>();
		HashMap<String, String> retVal = new HashMap<>();
		int index = 0;
		ArrayList<Payment> lista = new ArrayList<Payment> ();
		do{
			mapa.put("count", "20");
			mapa.put("start_index", String.valueOf(index));
			mapa.put("sort_by", "create_time");
			mapa.put("sort_order", "desc");
			RestTemplate restTemplate = new RestTemplate();
			Object[] parameters = new Object[] {mapa};
			String pattern = "https://api.sandbox.paypal.com/v1/payments/payment?count={0}&start_index={1}&sort_by={2}&sort_order={3}";
			String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		//	System.out.println(resourcePath);
		    ResponseEntity<String> bitcoinResponse = null;
		    HttpHeaders headers = new HttpHeaders();
		    headers.set("Content-Type", "application/json");
		    headers.set("Authorization", accessToken);
		//https://developer.paypal.com/docs/api/payments/v1/
			ResponseEntity<PaymentHistory> response = null;
			HttpEntity<?> entity = new HttpEntity<>(headers);
		//	System.out.println(entity.getHeaders().toString());
		    try {
		    	response = restTemplate.exchange(resourcePath, HttpMethod.GET, entity, PaymentHistory.class);
			} catch (RestClientException e) {
				e.printStackTrace();
				return null;
			}
		    lista = (ArrayList<Payment>) ((PaymentHistory) response.getBody()).getPayments();
			dodajUListu(mapa, lista);
			index+=20;
		}while(lista.size()==20);
		
	    return mapa;
	}
	private void dodajUListu(HashMap<String, String> retVal, ArrayList<Payment> payments) {
		for(Payment p : payments){
			retVal.put(p.getId(), p.getState());
			System.out.println("---------------------------------------");
			System.out.println(p.toString());
			System.out.println("---------------------------------------");
		}
	}
	
	
}
