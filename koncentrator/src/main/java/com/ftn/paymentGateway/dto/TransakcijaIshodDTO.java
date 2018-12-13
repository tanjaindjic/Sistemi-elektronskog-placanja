package com.ftn.paymentGateway.dto;

import com.ftn.paymentGateway.enumerations.TransakcijaStatus;

public class TransakcijaIshodDTO {
	
	private boolean uspesno;
	
	private boolean redirekcija;
	
	private TransakcijaStatus noviStatus;
	
	private Long izvrsnaTransakcija;
	
	private String novaPutanja;

	public TransakcijaIshodDTO() {
		super();
	}

	public TransakcijaIshodDTO(boolean uspesno, boolean redirekcija, TransakcijaStatus noviStatus,
			Long izvrsnaTransakcija, String novaPutanja) {
		super();
		this.uspesno = uspesno;
		this.redirekcija = redirekcija;
		this.noviStatus = noviStatus;
		this.izvrsnaTransakcija = izvrsnaTransakcija;
		this.novaPutanja = novaPutanja;
	}

	public boolean isUspesno() {
		return uspesno;
	}

	public void setUspesno(boolean uspesno) {
		this.uspesno = uspesno;
	}

	public boolean isRedirekcija() {
		return redirekcija;
	}

	public void setRedirekcija(boolean redirekcija) {
		this.redirekcija = redirekcija;
	}

	public TransakcijaStatus getNoviStatus() {
		return noviStatus;
	}

	public void setNoviStatus(TransakcijaStatus noviStatus) {
		this.noviStatus = noviStatus;
	}

	public Long getIzvrsnaTransakcija() {
		return izvrsnaTransakcija;
	}

	public void setIzvrsnaTransakcija(Long izvrsnaTransakcija) {
		this.izvrsnaTransakcija = izvrsnaTransakcija;
	}

	public String getNovaPutanja() {
		return novaPutanja;
	}

	public void setNovaPutanja(String novaPutanja) {
		this.novaPutanja = novaPutanja;
	}
	
}
