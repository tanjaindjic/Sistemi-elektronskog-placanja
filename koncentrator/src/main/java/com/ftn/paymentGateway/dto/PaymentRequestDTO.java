package com.ftn.paymentGateway.dto;

import javax.validation.constraints.*;

import com.ftn.paymentGateway.model.EntitetPlacanja;

public class PaymentRequestDTO {
	
	@NotNull
	private EntitetPlacanjaDTO entitetPlacanja;
	
	@NotNull
	private double iznos;
	
	@NotNull
	private boolean pretplata;
	
	@NotNull
	private Long maticnaTransakcija;

	public PaymentRequestDTO() {
		super();
	}

	public PaymentRequestDTO(@NotNull EntitetPlacanjaDTO entitetPlacanja, @NotNull double iznos,
			@NotNull Long maticnaTransakcija, @NotNull boolean pretplata) {
		super();
		this.entitetPlacanja = entitetPlacanja;
		this.iznos = iznos;
		this.maticnaTransakcija = maticnaTransakcija;
		this.pretplata = pretplata;
	}

	public EntitetPlacanjaDTO getEntitetPlacanja() {
		return entitetPlacanja;
	}

	public void setEntitetPlacanja(EntitetPlacanjaDTO entitetPlacanja) {
		this.entitetPlacanja = entitetPlacanja;
	}

	public double getIznos() {
		return iznos;
	}

	public void setIznos(double iznos) {
		this.iznos = iznos;
	}

	public Long getMaticnaTransakcija() {
		return maticnaTransakcija;
	}

	public void setMaticnaTransakcija(Long maticnaTransakcija) {
		this.maticnaTransakcija = maticnaTransakcija;
	}

	public boolean isPretplata() {
		return pretplata;
	}

	public void setPretplata(boolean pretplata) {
		this.pretplata = pretplata;
	}
	
}
