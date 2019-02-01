package com.ftn.paymentGateway.dto;

import javax.validation.constraints.NotNull;

public class EntitetPlacanjaDTO {
	
	private String identifikacioniKod;
	
	private EntitetPlacanjaDTO nadredjeni;

	public EntitetPlacanjaDTO() {
		super();
	}

	public EntitetPlacanjaDTO(String identifikacioniKod, EntitetPlacanjaDTO nadredjeni) {
		super();
		this.identifikacioniKod = identifikacioniKod;
		this.nadredjeni = nadredjeni;
	}

	public String getIdentifikacioniKod() {
		return identifikacioniKod;
	}

	public void setIdentifikacioniKod(String identifikacioniKod) {
		this.identifikacioniKod = identifikacioniKod;
	}

	public EntitetPlacanjaDTO getNadredjeni() {
		return nadredjeni;
	}

	public void setNadredjeni(EntitetPlacanjaDTO nadredjeni) {
		this.nadredjeni = nadredjeni;
	}
	
}
