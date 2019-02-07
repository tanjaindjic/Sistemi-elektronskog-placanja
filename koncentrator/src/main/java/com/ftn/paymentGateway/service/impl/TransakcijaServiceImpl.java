package com.ftn.paymentGateway.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.dto.TransakcijaIshodDTO;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.exceptions.TransactionUpdateExeption;
import com.ftn.paymentGateway.helpClasses.RandomStringGenerator;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.repository.TransakcijaRepository;
import com.ftn.paymentGateway.service.TransakcijaService;

@Service
public class TransakcijaServiceImpl implements TransakcijaService{
	
	@Autowired
	private TransakcijaRepository transakcijaRepository;
	
	@Autowired
	private RandomStringGenerator randomStringGenerator;
	
	@Value("${frontend.tokenLength}")
	private int len;
	
	@Value("${tokenExpiration.inMinutes}")
	private int tokenExpiration;

	@Override
	public Transakcija getById(Long id) {
		
		return transakcijaRepository.findById(id).get();
	}

	@Override
	public Transakcija insertNewTransaction(EntitetPlacanja entitetPlacanja, PaymentRequestDTO paymentInfo, boolean pretplata, String successUrl, String failedUrl, String errorUrl) {
		
		if(paymentInfo.getIznos() < 0.00 || entitetPlacanja == null) {
			return null;
		}
		
		String uniqueToken = generateUniqueToken();
		
		Transakcija newPayment = new Transakcija(null, paymentInfo.getMaticnaTransakcija(), null, paymentInfo.getIznos(),
				new Date(System.currentTimeMillis()), TransakcijaStatus.C, uniqueToken, pretplata, entitetPlacanja, null, successUrl, failedUrl, errorUrl);
		
		return transakcijaRepository.save(newPayment);
	}

	@Override
	public Transakcija getByJedinstveniToken(String jedinstveniToken) {
		
		return transakcijaRepository.findByJedinstveniToken(jedinstveniToken);
	}
	
	private String generateUniqueToken() {
		String retVal = randomStringGenerator.genRandomString(len);
		
		if(transakcijaRepository.findByJedinstveniToken(retVal) == null) {
			return retVal;
		}
		return generateUniqueToken();
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Transakcija update(TransakcijaIshodDTO transakcijaIshod, Transakcija transakcija) throws TransactionUpdateExeption{
		
		if(transakcija == null || transakcijaIshod == null) {
			throw new TransactionUpdateExeption();
		}
		
		if(!transakcijaIshod.isUspesno()) {
			transakcija.setStatus(TransakcijaStatus.N);
			transakcija = transakcijaRepository.save(transakcija);
			return transakcija;
		}
		
		transakcija.setStatus(transakcijaIshod.getNoviStatus());
		transakcija.setIzvrsnaTransakcija(transakcijaIshod.getIzvrsnaTransakcija());
		
		return transakcijaRepository.save(transakcija);
	}

	@Override
	public Transakcija checkTokenValidity(Transakcija transakcija) {
		
		Date startDate = transakcija.getVreme();
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(startDate);
	    calendar.add(Calendar.MINUTE, tokenExpiration);
		Date endDate = calendar.getTime();
				
		return transakcijaRepository.checkTokenValidity(transakcija.getJedinstveniToken(), startDate, endDate);
	}

	@Override
	public Transakcija findByIzvrsnaTransakcija(String decode) {
		return transakcijaRepository.findByIzvrsnaTransakcija(decode);
	}

	@Override
	public Transakcija save(Transakcija transakcija) {
		return transakcijaRepository.save(transakcija);
	}

	@Override
	public List<Transakcija> get10ByStatusAndType(TransakcijaStatus status, TipPlacanja tipPlacanja) {
		
		return transakcijaRepository.findFirst10ByStatusAndTipPlacanja(status, tipPlacanja);
	}

}
