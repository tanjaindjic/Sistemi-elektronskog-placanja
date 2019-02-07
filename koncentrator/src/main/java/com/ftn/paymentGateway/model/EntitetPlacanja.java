package com.ftn.paymentGateway.model;

import javax.persistence.*;

@Entity
public class EntitetPlacanja {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, length = 120)
	private String naziv;
	
	@Column(nullable = false, length = 10, unique = true)
	private String identifikacioniKod;
	
	@Column(nullable = false, length = 10)
	private boolean poslovniSaradnik;
	
	@ManyToOne(optional = true)
	private EntitetPlacanja nadredjeni;
	
	@Column(nullable = true, length = 1000)
	private String syncPath;

	public EntitetPlacanja() {
		super();
	}

	public EntitetPlacanja(Long id, String naziv, String identifikacioniKod, boolean poslovniSaradnik,
			EntitetPlacanja nadredjeni, String syncPath) {
		super();
		this.id = id;
		this.naziv = naziv;
		this.identifikacioniKod = identifikacioniKod;
		this.poslovniSaradnik = poslovniSaradnik;
		this.nadredjeni = nadredjeni;
		this.syncPath = syncPath;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public String getIdentifikacioniKod() {
		return identifikacioniKod;
	}

	public void setIdentifikacioniKod(String identifikacioniKod) {
		this.identifikacioniKod = identifikacioniKod;
	}

	public boolean isPoslovniSaradnik() {
		return poslovniSaradnik;
	}

	public void setPoslovniSaradnik(boolean poslovniSaradnik) {
		this.poslovniSaradnik = poslovniSaradnik;
	}

	public EntitetPlacanja getNadredjeni() {
		return nadredjeni;
	}

	public void setNadredjeni(EntitetPlacanja nadredjeni) {
		this.nadredjeni = nadredjeni;
	}

	public String getSyncPath() {
		return syncPath;
	}

	public void setSyncPath(String syncPath) {
		this.syncPath = syncPath;
	}

}
