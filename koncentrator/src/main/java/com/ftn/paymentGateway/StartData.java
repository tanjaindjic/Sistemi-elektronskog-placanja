package com.ftn.paymentGateway;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ftn.paymentGateway.enumerations.IdPoljePlacanja;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.helpClasses.RandomStringGenerator;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.PoljePodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.repository.EntitetPlacanjaRepository;
import com.ftn.paymentGateway.repository.PodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.repository.PoljePodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.repository.TipPlacanjaRepository;
import com.ftn.paymentGateway.repository.TransakcijaRepository;

@Component
public class StartData {
	
	@Autowired
	private EntitetPlacanjaRepository entitetPlacanjaRepository;
	
	@Autowired
	private PodrzanoPlacanjeRepository podrzanoPlacanjeRepository;
	
	@Autowired
	private TipPlacanjaRepository tipPlacanjaRepository;
	
	@Autowired 
	private TransakcijaRepository transakcijaRepository;
	
	@Autowired
	private RandomStringGenerator randomStringGenerator;
	@Autowired 
	private PoljePodrzanoPlacanjeRepository poljePodrzanoPlacanjeRepository;
	
	
	public StartData() {}
	
	@PostConstruct
	private void init() {
		TipPlacanja tp1 = new TipPlacanja(null, "CCP", "Kreditna Kartica", "CreditCardPayment", "../../paymentGateway/assets/images/cc.png");
		TipPlacanja tp2 = new TipPlacanja(null, "PPP", "Pay Pal", "PayPalPayment", "../../paymentGateway/assets/images/pp.png");
		TipPlacanja tp3 = new TipPlacanja(null, "BCP", "Bitcoin", "BitcoinPayment", "../../paymentGateway/assets/images/bc.png");
		
		tp1 = tipPlacanjaRepository.save(tp1);
		tp2 = tipPlacanjaRepository.save(tp2);
		tp3 = tipPlacanjaRepository.save(tp3);
		
		/* Naucne Centrale */
		EntitetPlacanja ep1 = new EntitetPlacanja(null, "Naucna Centrala 1", "NC1NC1NC1#", true, null);
		EntitetPlacanja ep2 = new EntitetPlacanja(null, "Naucna Centrala 2", "NC2NC2NC2#", true, null);
		
		
		/* Casopisi */
		EntitetPlacanja ep3 = new EntitetPlacanja(null, "Casopis 1", "CAS1#CAS1#", false, ep1);
		EntitetPlacanja ep4 = new EntitetPlacanja(null, "Casopis 2", "CAS2#CAS2#", false, ep1);
		EntitetPlacanja ep5 = new EntitetPlacanja(null, "Casopis 3", "CAS3#CAS3#", false, ep2);
		EntitetPlacanja ep6 = new EntitetPlacanja(null, "Casopis 4", "CAS4#CAS4#", false, ep2);
		
		ep1 = entitetPlacanjaRepository.save(ep1);
		ep2 = entitetPlacanjaRepository.save(ep2);
		ep3 = entitetPlacanjaRepository.save(ep3);
		ep4 = entitetPlacanjaRepository.save(ep4);
		ep5 = entitetPlacanjaRepository.save(ep5);
		ep6 = entitetPlacanjaRepository.save(ep6);
		
		/* Kreditne Kartice */
		PoljePodrzanoPlacanje ppb1 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, "1");
		PoljePodrzanoPlacanje ppb2 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, "2");
		PoljePodrzanoPlacanje ppb3 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, "3");
		PoljePodrzanoPlacanje ppb11 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_PASSWORD, "pass1");
		PoljePodrzanoPlacanje ppb21 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_PASSWORD, "pass2");
		PoljePodrzanoPlacanje ppb31 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_PASSWORD, "pass3");
		
		ppb1 = poljePodrzanoPlacanjeRepository.save(ppb1);
		ppb2 = poljePodrzanoPlacanjeRepository.save(ppb2);
		ppb3 = poljePodrzanoPlacanjeRepository.save(ppb3);
		ppb11 = poljePodrzanoPlacanjeRepository.save(ppb11);
		ppb21 = poljePodrzanoPlacanjeRepository.save(ppb21);
		ppb31 = poljePodrzanoPlacanjeRepository.save(ppb31);
		
		List<PoljePodrzanoPlacanje> banka1PP = new ArrayList<>();
		banka1PP.add(ppb1);
		banka1PP.add(ppb11);
		
		List<PoljePodrzanoPlacanje> banka2PP = new ArrayList<>();
		banka2PP.add(ppb2);
		banka2PP.add(ppb21);
		
		List<PoljePodrzanoPlacanje> banka3PP = new ArrayList<>();
		banka3PP.add(ppb3);
		banka3PP.add(ppb31);
		
		
		PodrzanoPlacanje pp1 = new PodrzanoPlacanje(null, false, banka1PP, ep1, tp1);
		PodrzanoPlacanje pp2 = new PodrzanoPlacanje(null, false, banka2PP, ep2, tp1);
		PodrzanoPlacanje pp3 = new PodrzanoPlacanje(null, false, banka3PP, ep3, tp1);
		
		pp1 = podrzanoPlacanjeRepository.save(pp1);
		pp2 = podrzanoPlacanjeRepository.save(pp2);
		pp3 = podrzanoPlacanjeRepository.save(pp3);
		
		
		/* PayPal */
		PoljePodrzanoPlacanje pppp1 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, "AS5IMk8HPdQhY6_LF4MismGdr9j73ERw2K9fYVhtH1O5cTQPqX5ec5vaEm5MlSl8GosBEczyC8UcJo1-");
		PoljePodrzanoPlacanje pppp2 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, "AS5IMk8HPdQhY6_LF4MismGdr9j73ERw2K9fYVhtH1O5cTQPqX5ec5vaEm5MlSl8GosBEczyC8UcJo1-");
		PoljePodrzanoPlacanje pppp11 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_PASSWORD, "EHijiYCc4J0QRBlB475fXO23eUBSatQONjw-OaGrTjPgEv2J-uKEBkLFsQuSeFFjX9KwMIxkjXbS_yjw");
		PoljePodrzanoPlacanje pppp21 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_PASSWORD, "EHijiYCc4J0QRBlB475fXO23eUBSatQONjw-OaGrTjPgEv2J-uKEBkLFsQuSeFFjX9KwMIxkjXbS_yjw");
		

		pppp1 = poljePodrzanoPlacanjeRepository.save(pppp1);
		pppp11 = poljePodrzanoPlacanjeRepository.save(pppp11);
		pppp2 = poljePodrzanoPlacanjeRepository.save(pppp2);
		pppp21 = poljePodrzanoPlacanjeRepository.save(pppp21);
		
		List<PoljePodrzanoPlacanje> pppp1PP = new ArrayList<>();
		pppp1PP.add(pppp1);
		pppp1PP.add(pppp11);
		
		List<PoljePodrzanoPlacanje> pppp2PP = new ArrayList<>();
		pppp2PP.add(pppp2);
		pppp2PP.add(pppp21);
		

		PodrzanoPlacanje pp4 = new PodrzanoPlacanje(null, false, pppp1PP, ep3, tp2);
		PodrzanoPlacanje pp5 = new PodrzanoPlacanje(null, false, pppp2PP, ep4, tp2);
		
		pp4 = podrzanoPlacanjeRepository.save(pp4);
		pp5 = podrzanoPlacanjeRepository.save(pp5);
		
		/* Bitcoin */
		PoljePodrzanoPlacanje ppbc1 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, "vXSzu6yK-XC9Gf1B2_TaS3Pfdp4bkefDsyxD7yXi");
		PoljePodrzanoPlacanje ppbc2 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, "vXSzu6yK-XC9Gf1B2_TaS3Pfdp4bkefDsyxD7yXi");
		
		ppbc1 = poljePodrzanoPlacanjeRepository.save(ppbc1);
		ppbc2 = poljePodrzanoPlacanjeRepository.save(ppbc2);
		
		List<PoljePodrzanoPlacanje> ppbc1PP = new ArrayList<>();
		ppbc1PP.add(ppbc1);
		
		List<PoljePodrzanoPlacanje> ppbc2PP = new ArrayList<>();
		ppbc2PP.add(ppbc2);
		
		PodrzanoPlacanje pp6 = new PodrzanoPlacanje(null, false, ppbc1PP, ep3, tp3);
		PodrzanoPlacanje pp7 = new PodrzanoPlacanje(null, false, ppbc2PP, ep4, tp3);
		
		pp6 = podrzanoPlacanjeRepository.save(pp6);
		pp7 = podrzanoPlacanjeRepository.save(pp7);
		
		String token1 = randomStringGenerator.genRandomString(90);
		String token2 = randomStringGenerator.genRandomString(90);
		String token3 = randomStringGenerator.genRandomString(90);
		String token4 = randomStringGenerator.genRandomString(90);
		String token5 = randomStringGenerator.genRandomString(90);
		
		/* Transakcije */
		Transakcija tr1 = new Transakcija(null, new Long(1), null, 5.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token1, false, ep3, tp1, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
		Transakcija tr2 = new Transakcija(null, new Long(2), null, 9.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token2, false, ep4, tp1, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
		Transakcija tr3 = new Transakcija(null, new Long(3), null, 12.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token3, false, ep5, tp1, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
		Transakcija tr4 = new Transakcija(null, new Long(4), "152975", 9.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token4, false, ep3, tp3, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
		Transakcija tr5 = new Transakcija(null, new Long(5), "152976", 12.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token5, false, ep3, tp3, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
		
		Transakcija tr6 = new Transakcija(null, new Long(6), null, 11.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, "AAA", false, ep3, null, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
		Transakcija tr7 = new Transakcija(null, new Long(7), null, 7.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, "BBB", false, ep4, null, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
		
		transakcijaRepository.save(tr1);
		transakcijaRepository.save(tr2);
		transakcijaRepository.save(tr3);
		transakcijaRepository.save(tr4);
		transakcijaRepository.save(tr5);
		transakcijaRepository.save(tr6);
		transakcijaRepository.save(tr7);
		
	}

}
