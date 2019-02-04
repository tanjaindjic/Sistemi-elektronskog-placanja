package com.ftn.paymentGateway.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.helpClasses.RSAEncryptDecrypt;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.PoljePodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.repository.PodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.service.PodrzanoPlacanjeService;

@Service
public class PodrzanoPlacanjeServiceImpl implements PodrzanoPlacanjeService{

	@Autowired 
	private PodrzanoPlacanjeRepository podrzanoPlacanjeRepository;

	@Autowired 
	private RSAEncryptDecrypt rsa;
	
	@Override
	public PodrzanoPlacanje insert(PodrzanoPlacanje podrzanoPlacanje) {
	/*	for(PoljePodrzanoPlacanje ppp : podrzanoPlacanje.getPolja()){
			try {
				String encrypt = rsa.encrypt(ppp.getVrednost());
				System.out.println(".................. "+ppp.getVrednost());
				ppp.setVrednost(encrypt);
				System.out.println("uspeo da kriptuje "+ encrypt);
			} catch (Exception e) {
				System.out.println("nije uspeo da kriptuje............");
				e.printStackTrace();
			}			
		}*/
		return podrzanoPlacanjeRepository.save(podrzanoPlacanje);
	}

	@Override
	public PodrzanoPlacanje getById(Long id) {
		
		return podrzanoPlacanjeRepository.getOne(id);
	}

	@Override
	public ArrayList<PodrzanoPlacanje> getByEntitetPlacanjaAndTipPlacanja(EntitetPlacanja entitetPlacanja,
			TipPlacanja tipPlacanja) {
		
		return podrzanoPlacanjeRepository.findByEntitetPlacanjaAndTipPlacanja(entitetPlacanja, tipPlacanja);
	}

	@Override
	public ArrayList<PodrzanoPlacanje> getByEntitetPlacanja(EntitetPlacanja entitetPlacanja) {
		
		return podrzanoPlacanjeRepository.findByEntitetPlacanja(entitetPlacanja);
	}
	
}
