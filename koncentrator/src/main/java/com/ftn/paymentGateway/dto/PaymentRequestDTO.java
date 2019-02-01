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
	
	@NotNull
    private String successURL;
    
	@NotNull
    private String failedURL;
    
	@NotNull
    private String errorURL;

	public PaymentRequestDTO() {
		super();
	}

	public PaymentRequestDTO(@NotNull EntitetPlacanjaDTO entitetPlacanja, @NotNull double iznos,
			@NotNull boolean pretplata, @NotNull Long maticnaTransakcija, @NotNull String successURL,
			@NotNull String failedURL, @NotNull String errorURL) {
		super();
		this.entitetPlacanja = entitetPlacanja;
		this.iznos = iznos;
		this.pretplata = pretplata;
		this.maticnaTransakcija = maticnaTransakcija;
		this.successURL = successURL;
		this.failedURL = failedURL;
		this.errorURL = errorURL;
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

	public boolean isPretplata() {
		return pretplata;
	}

	public void setPretplata(boolean pretplata) {
		this.pretplata = pretplata;
	}

	public Long getMaticnaTransakcija() {
		return maticnaTransakcija;
	}

	public void setMaticnaTransakcija(Long maticnaTransakcija) {
		this.maticnaTransakcija = maticnaTransakcija;
	}

	public String getSuccessURL() {
		return successURL;
	}

	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}

	public String getFailedURL() {
		return failedURL;
	}

	public void setFailedURL(String failedURL) {
		this.failedURL = failedURL;
	}

	public String getErrorURL() {
		return errorURL;
	}

	public void setErrorURL(String errorURL) {
		this.errorURL = errorURL;
	}
	
}
