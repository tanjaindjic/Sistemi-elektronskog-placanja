package com.ftn.paymentGateway.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ftn.paymentGateway.dto.BankResponseDTO;
import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.dto.PaymentResponseDTO;
import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.InvalidPaymentTypeException;
import com.ftn.paymentGateway.exceptions.PaymentErrorException;
import com.ftn.paymentGateway.exceptions.TransactionUpdateExeption;
import com.ftn.paymentGateway.exceptions.UnsupportedMethodException;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.paymentStrategy.PaymentFactory;
import com.ftn.paymentGateway.paymentStrategy.impl.PayPalPayment;
import com.ftn.paymentGateway.service.EntitetPlacanjaService;
import com.ftn.paymentGateway.service.PodrzanoPlacanjeService;
import com.ftn.paymentGateway.service.TipPlacanjaService;
import com.ftn.paymentGateway.service.TransakcijaService;

@RestController
@RequestMapping(value = "/rest/")
public class PaymentController {
	
	@Value("${frontend.redirectURL}")
	private String redirectionUrl;
	
	@Autowired 
	private EntitetPlacanjaService entitetPlacanjaService;
	
	@Autowired
	private TipPlacanjaService tipPlacanjaService;
	
	@Autowired
	private PodrzanoPlacanjeService podrzanoPlacanjeService;
	
	@Autowired
	private TransakcijaService transakcijaService;
	
	@Autowired 
	private PaymentFactory paymentFactory;
	@Autowired 
	private PayPalPayment payPalPayment;
	
	
	@RequestMapping(value = "sendPaymentRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaymentResponseDTO> recivePayment(@Valid @RequestBody PaymentRequestDTO paymentRequest, BindingResult bindingResult) throws URISyntaxException, UnsupportedEncodingException {
		
		if(bindingResult.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(!entitetPlacanjaService.validateChain(paymentRequest.getEntitetPlacanja())) {
			return new ResponseEntity<PaymentResponseDTO>(new PaymentResponseDTO(paymentRequest.getMaticnaTransakcija(), TransakcijaStatus.N, "Neispravan entitet placanja."), HttpStatus.OK);
		}
		
		EntitetPlacanja ep = entitetPlacanjaService.getByIdentifikacioniKod(paymentRequest.getEntitetPlacanja().getIdentifikacioniKod());
		
		Transakcija novaTransakcija = transakcijaService.insertNewTransaction(ep, paymentRequest, paymentRequest.isPretplata(), paymentRequest.getSuccessURL(), paymentRequest.getFailedURL(), paymentRequest.getErrorURL());
		
		if(novaTransakcija == null) {
			return new ResponseEntity<PaymentResponseDTO>(new PaymentResponseDTO(paymentRequest.getMaticnaTransakcija(), TransakcijaStatus.N, "Neuspesno placanje, pokusajte kasnije."), HttpStatus.OK);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", redirectionUrl+novaTransakcija.getJedinstveniToken());
		
		return new ResponseEntity<PaymentResponseDTO>(new PaymentResponseDTO(paymentRequest.getMaticnaTransakcija(), TransakcijaStatus.C, "Transakcija je uspesno zabelezena, bicete preusmereni na panel za placanje."), headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "doPayment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> doPayment(
			@RequestParam(value="paymentTypeId", required = true) Long paymentTypeId,
			@RequestParam(value="uniqueToken", required = true) String uniqueToken){
		
		Transakcija transakcija = transakcijaService.getByJedinstveniToken(uniqueToken);
		
		if(transakcija == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(transakcijaService.checkTokenValidity(transakcija) == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		TipPlacanja tipPlacanja = tipPlacanjaService.getById(paymentTypeId);
		
		if(tipPlacanja == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		ArrayList<PodrzanoPlacanje> podrzanaPlacanja = podrzanoPlacanjeService.getByEntitetPlacanjaAndTipPlacanja(transakcija.getEntitetPlacanja(), tipPlacanja);
		
		if(podrzanaPlacanja.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		transakcija.setTipPlacanja(tipPlacanja);
		transakcijaService.save(transakcija);
		PodrzanoPlacanje podrzanoPlacanje = podrzanaPlacanja.get(0);
		
		TransakcijaIshodDTO retVal = null;
		try {
			retVal = paymentFactory.getPaymentStrategy(tipPlacanja).doPayment(transakcija, podrzanoPlacanje);
		} catch (InvalidPaymentTypeException | PaymentErrorException e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		try {
			transakcijaService.update(retVal, transakcija);
		} catch (TransactionUpdateExeption e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(retVal.isRedirekcija()) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Location", retVal.getNovaPutanja());
			headers.add("Access-Control-Allow-Origin", "*");
			return new ResponseEntity<Boolean>(retVal.isUspesno(), headers, HttpStatus.OK);
		}
	    
		return new ResponseEntity<Boolean>(retVal.isUspesno(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "success", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView completePayment(HttpServletRequest request, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, @RequestParam("token") String token){
		Transakcija transakcija = transakcijaService.findByIzvrsnaTransakcija(request.getParameter("paymentId"));
		System.out.println(transakcija.toString());
	//	System.out.println("prvo: "+transakcija.getTipPlacanja().getId());
	//	System.out.println("drugo: "+tipPlacanjaService.getByKod("PPP").getId());
		TipPlacanja tipPlacanja = tipPlacanjaService.getById(transakcija.getTipPlacanja().getId());
		ArrayList<PodrzanoPlacanje> podrzanaPlacanja = podrzanoPlacanjeService.getByEntitetPlacanjaAndTipPlacanja(transakcija.getEntitetPlacanja(), tipPlacanja);
		
		if(podrzanaPlacanja.isEmpty()) {
			return null;
		}
		
		PodrzanoPlacanje podrzanoPlacanje = podrzanaPlacanja.get(0);
		Boolean retVal= false;
		try {
			retVal = paymentFactory.getPaymentStrategy(tipPlacanja).completePayment(request, podrzanoPlacanje);
			if(retVal==null){
				return null;
			}
			System.out.println("USPESNO RADI ZA REDIREKCIJU");
		} catch (UnsupportedMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPaymentTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String urlRedirect = "https://localhost:8098/paymentGateway/#!/home";
		PaymentResponseDTO response = new PaymentResponseDTO();
		if(retVal){		
			System.out.println("USPESNO zavrsio ZA PAYPAL REDIREKCIJU");		        
	        response.setMaticnaTransakcija(transakcija.getMaticnaTransakcija());
	        response.setPoruka("Uplata je uspesno izvrsena");
	        response.setStatus(TransakcijaStatus.U);
	        transakcija.setStatus(TransakcijaStatus.U);
	        transakcijaService.save(transakcija);
	        System.out.println("NINA CAREEEEE");
	        urlRedirect = "https://localhost:8098/paymentGateway/#!/success/"+transakcija.getJedinstveniToken();
	        return new ModelAndView("redirect:" + urlRedirect);
		//TODO dodati redirekciju na odgovarajucu stranicu i za controller za "/cancel"
		}
		else{
			System.out.println("NEUSPESNO zarsio ZA PAYPAL REDIREKCIJU");
            response.setPoruka("Uplata je NIJE uspesno izvrsena");
            response.setStatus(TransakcijaStatus.N);
            transakcija.setStatus(TransakcijaStatus.N);
        }
        transakcijaService.save(transakcija);
		return new ModelAndView("redirect:" + urlRedirect);
    }
	
	@RequestMapping(value = "bankResponse", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes= MediaType.APPLICATION_JSON_VALUE)
	public Boolean completePayment(@RequestBody BankResponseDTO bankResponse, HttpServletRequest request){
		System.out.println("usao u bank response");
		System.out.println(bankResponse.toString());
		Transakcija transakcija = transakcijaService.getById(bankResponse.getMerchantOrderID());
		TipPlacanja tipPlacanja = tipPlacanjaService.getById(transakcija.getTipPlacanja().getId());
		ArrayList<PodrzanoPlacanje> podrzanaPlacanja = podrzanoPlacanjeService.getByEntitetPlacanjaAndTipPlacanja(transakcija.getEntitetPlacanja(), tipPlacanja);
		
		if(podrzanaPlacanja.isEmpty()) {
			return false;
		}
		
		PodrzanoPlacanje podrzanoPlacanje = podrzanaPlacanja.get(0);
		Boolean retVal= false;
		try {
			retVal = paymentFactory.getPaymentStrategy(tipPlacanja).completePayment(request, podrzanoPlacanje);
			if(retVal==null){
				return false;
			}
			System.out.println("USPESNO RADI ZA BANKU success");
		} catch (UnsupportedMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPaymentTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String urlRedirect = "https://localhost:8098/paymentGateway/#!/home";
		PaymentResponseDTO response = new PaymentResponseDTO();
		if(retVal){		
			System.out.println("USPESNO zavrsio ZA BANKU REDIREKCIJU");		        
	        response.setMaticnaTransakcija(transakcija.getMaticnaTransakcija());
	        response.setPoruka("Uplata je uspesno izvrsena");
	        response.setStatus(TransakcijaStatus.U);
	        transakcija.setStatus(TransakcijaStatus.U);
	        System.out.println("TANJA CAREEEEE");
	        urlRedirect = "https://localhost:8098/paymentGateway/#!/success/"+transakcija.getJedinstveniToken();
	        return true;
		//TODO dodati redirekciju na odgovarajucu stranicu i za controller za "/cancel"
		}
		else{
			System.out.println("NIJE USPESNO zavrsio ZA BANKU REDIREKCIJU");
            response.setPoruka("Uplata je NIJE uspesno izvrsena");
            response.setStatus(TransakcijaStatus.N);
            transakcija.setStatus(TransakcijaStatus.N);
        }
        transakcijaService.save(transakcija);
		return false;
    }
	
	@RequestMapping(value = "proveriStatusTransakcije", method = RequestMethod.GET)
	public ResponseEntity<TransakcijaStatus> proveriStatusTransakcije(@RequestParam("uniqueToken") String uniqueToken) throws URISyntaxException, UnsupportedEncodingException {
		
		if(uniqueToken.isEmpty() || uniqueToken==null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Transakcija transakcija = transakcijaService.getByJedinstveniToken(uniqueToken);
		if(transakcija==null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			
		}
		return new ResponseEntity<TransakcijaStatus>(transakcija.getStatus(), HttpStatus.OK);

	}
	
	@RequestMapping(value = "obaviVracanje", method = RequestMethod.GET)
	public ResponseEntity<Boolean> obaviVracanje(@RequestParam("uniqueToken") String uniqueToken) throws URISyntaxException, UnsupportedEncodingException {		
		if(uniqueToken.isEmpty() || uniqueToken==null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Transakcija transakcija = transakcijaService.getByJedinstveniToken(uniqueToken);
		if(transakcija==null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			
		}
		
		RestTemplate restTemplate = new RestTemplate();
		HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
		Boolean uspesno = false;
		if(transakcija.getStatus().equals(TransakcijaStatus.U))
			uspesno=true;
		TransakcijaIshodDTO retVal = new TransakcijaIshodDTO(uspesno, true, transakcija.getStatus(), entitetPlacanjaService.getUrlResponse(transakcija.getEntitetPlacanja()), "");
	    ResponseEntity<TransakcijaIshodDTO> response = null;
		try {
			String retUrl = entitetPlacanjaService.getUrlResponse(transakcija.getEntitetPlacanja());
			System.out.println("URL: "+retUrl);			
			response = restTemplate.postForEntity(new URI(retUrl), retVal, TransakcijaIshodDTO.class);
		} catch (RestClientException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", entitetPlacanjaService.getUrlLocation(transakcija.getEntitetPlacanja()));
		headers.add("Access-Control-Allow-Origin", "*");
		return new ResponseEntity<Boolean>(true, headers, HttpStatus.OK);

	}
	
	
}
