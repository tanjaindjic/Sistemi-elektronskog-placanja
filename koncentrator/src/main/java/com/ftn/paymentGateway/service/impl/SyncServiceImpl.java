package com.ftn.paymentGateway.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.ftn.paymentGateway.enumerations.SyncStatus;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.repository.EntitetPlacanjaRepository;
import com.ftn.paymentGateway.repository.TransakcijaRepository;
import com.ftn.paymentGateway.service.SyncService;

@Service
public class SyncServiceImpl implements SyncService{
	
	@Value("${frontend.tokenLength}")
	private int len;
	
	@Value("${tokenExpiration.inMinutes}")
	private int tokenExpiration;
	
	@Autowired
	private TransakcijaRepository transakcijaRepository;
	
	@Autowired
	private EntitetPlacanjaRepository entitetPlacanjaRepository;
	
	@Override
	@Scheduled(initialDelay = 7000, fixedRate = 60000)
	public void posaljiSaradnicima() {
		
		List<EntitetPlacanja> poslovniSaradnici = entitetPlacanjaRepository.findByPoslovniSaradnik(true);
		
		for(EntitetPlacanja poslovniSaradnik : poslovniSaradnici) {
			
			Map<Long, String> transakcijeInfo = new HashMap<Long, String>();
			List<Transakcija> zaIzmenu = new ArrayList<Transakcija>(); 
			
			transakcijeInfo = buildResponseMap(poslovniSaradnik, transakcijeInfo, zaIzmenu);
			
			HttpEntity<Map<Long, String>> syncRequest = new HttpEntity<Map<Long, String>>(transakcijeInfo);
			
			RestTemplate restTemplate = new RestTemplate();
			HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);

		    ResponseEntity<String> syncResponse = null;
		    try {
		    	syncResponse = restTemplate.postForEntity(new URI(poslovniSaradnik.getSyncPath()), syncRequest, String.class);
			} catch (Exception e) {
				//e.printStackTrace();
				continue;
			}
		    
		    if(syncResponse.getBody().equals(SyncStatus.SUCCESS.toString())){
		    	for(Transakcija tempTran : zaIzmenu) {
		    		setEvidentirano(tempTran);
		    	}
		    }
			
		}
	}
	
	private Map<Long, String> buildResponseMap(EntitetPlacanja entitetPlacanja, Map<Long, String> transakcijeInfo, List<Transakcija> zaIzmenu){
		
		List<Transakcija> zaEvidentiranje = transakcijaRepository.findByEntitetPlacanjaAndPoslatoSaradniku(entitetPlacanja, false);
		
		for(Transakcija tempTran : zaEvidentiranje) {
			transakcijeInfo.put(tempTran.getMaticnaTransakcija(), tempTran.getStatus().toString());
			zaIzmenu.add(tempTran);
		}
		
		List<EntitetPlacanja> deca = entitetPlacanjaRepository.findByNadredjeni(entitetPlacanja);
		
		if(!deca.isEmpty()) {
			for(EntitetPlacanja dete : deca) {
				transakcijeInfo.putAll(buildResponseMap(dete, transakcijeInfo, zaIzmenu)); 
			}
		}
		
		return transakcijeInfo;
	}
	
	@Override
	@Scheduled(initialDelay = 5000, fixedRate = 60000)
	public void disableExpired() {
		
		List<Transakcija> zaIzmenu = transakcijaRepository.findByStatus(TransakcijaStatus.C);
			
		for(Transakcija t : zaIzmenu) {
			if(!checkTokenValidity(t)) {
				setStatus(t, TransakcijaStatus.E);
			}
		}
		
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	private void setEvidentirano(Transakcija transakcija) {
		transakcija.setPoslatoSaradniku(true);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	private void setStatus(Transakcija transakcija, TransakcijaStatus noviStatus) {
		transakcija.setStatus(noviStatus);
		transakcijaRepository.save(transakcija);
	}
	
	
	private boolean checkTokenValidity(Transakcija transakcija) {
		
		Date startDate = transakcija.getVreme();
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(startDate);
	    calendar.add(Calendar.MINUTE, tokenExpiration);
		Date endDate = calendar.getTime();
		
		if(endDate.before(new Date(System.currentTimeMillis()))) {
			return false;
		}
				
		return true;
	}

}
