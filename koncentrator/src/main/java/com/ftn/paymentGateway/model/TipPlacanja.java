package com.ftn.paymentGateway.model;

import javax.persistence.*;

@Entity
public class TipPlacanja {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, length = 3, unique = true)
	private String kod;
	
	@Column(nullable = false, length = 120)
	private String naziv;
	
	@Column(nullable = true, length = 120)
	private String klasa;
	
	@Column(nullable = true)
	private String putanjaSlike;

	public TipPlacanja() {
		super();
	}

	public TipPlacanja(Long id, String kod, String naziv, String klasa, String putanjaSlike) {
		super();
		this.id = id;
		this.kod = kod;
		this.naziv = naziv;
		this.klasa = klasa;
		this.putanjaSlike = putanjaSlike;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKod() {
		return kod;
	}

	public void setKod(String kod) {
		this.kod = kod;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public String getKlasa() {
		return klasa;
	}

	public void setKlasa(String klasa) {
		this.klasa = klasa;
	}

	public String getPutanjaSlike() {
		return putanjaSlike;
	}

	public void setPutanjaSlike(String putanjaSlike) {
		this.putanjaSlike = putanjaSlike;
	}
	
}
