package com.ftn.paymentGateway.dto;

import javax.validation.constraints.*;

import com.ftn.paymentGateway.enumerations.TransakcijaStatus;

public class PaymentResponseDTO {
	
	@NotNull
	private Long maticnaTransakcija;
	
	@NotNull
	private TransakcijaStatus status;
	
	@Size(max = 1500)
	private String poruka;

	public PaymentResponseDTO() {
		super();
	}

	public PaymentResponseDTO(@NotNull Long maticnaTransakcija, @NotNull TransakcijaStatus status, @Size(max = 1500) String poruka) {
		super();
		this.maticnaTransakcija = maticnaTransakcija;
		this.status = status;
		this.poruka = poruka;
	}

	public Long getMaticnaTransakcija() {
		return maticnaTransakcija;
	}

	public void setMaticnaTransakcija(Long maticnaTransakcija) {
		this.maticnaTransakcija = maticnaTransakcija;
	}

	public TransakcijaStatus getStatus() {
		return status;
	}

	public void setStatus(TransakcijaStatus status) {
		this.status = status;
	}

	public String getPoruka() {
		return poruka;
	}

	public void setPoruka(String poruka) {
		this.poruka = poruka;
	}
	
}
