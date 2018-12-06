package com.ftn.paymentGateway.dto;

import javax.validation.constraints.*;

import com.ftn.paymentGateway.model.EntitetPlacanja;

public class PaymentRequestDTO {
	
	@NotNull
	private EntitetPlacanjaDTO entitetPlacanja;
	
	@NotNull
	private double iznos;
	
	@NotNull
	private Long maticnaTransakcija;

	public PaymentRequestDTO() {
		super();
	}

	public PaymentRequestDTO(@NotNull EntitetPlacanjaDTO entitetPlacanja, @NotNull double iznos,
			@NotNull Long maticnaTransakcija) {
		super();
		this.entitetPlacanja = entitetPlacanja;
		this.iznos = iznos;
		this.maticnaTransakcija = maticnaTransakcija;
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
	
}
