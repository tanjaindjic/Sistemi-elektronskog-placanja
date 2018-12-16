package com.ftn.paymentGateway.model;

import javax.persistence.*;

@Entity
public class PodrzanoPlacanje {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, length = 120)
	private String idNaloga;
	
	@Column(nullable = true, length = 120)
	private String sifraNaloga;
	
	@ManyToOne(optional = true)
	private EntitetPlacanja entitetPlacanja;
	
	@ManyToOne(optional = true)
	private TipPlacanja tipPlacanja;

	public PodrzanoPlacanje() {
		super();
	}

	public PodrzanoPlacanje(Long id, String idNaloga, String sifraNaloga, EntitetPlacanja entitetPlacanja,
			TipPlacanja tipPlacanja) {
		super();
		this.id = id;
		this.idNaloga = idNaloga;
		this.sifraNaloga = sifraNaloga;
		this.entitetPlacanja = entitetPlacanja;
		this.tipPlacanja = tipPlacanja;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdNaloga() {
		return idNaloga;
	}

	public void setIdNaloga(String idNaloga) {
		this.idNaloga = idNaloga;
	}

	public String getSifraNaloga() {
		return sifraNaloga;
	}

	public void setSifraNaloga(String sifraNaloga) {
		this.sifraNaloga = sifraNaloga;
	}

	public EntitetPlacanja getEntitetPlacanja() {
		return entitetPlacanja;
	}

	public void setEntitetPlacanja(EntitetPlacanja entitetPlacanja) {
		this.entitetPlacanja = entitetPlacanja;
	}

	public TipPlacanja getTipPlacanja() {
		return tipPlacanja;
	}

	public void setTipPlacanja(TipPlacanja tipPlacanja) {
		this.tipPlacanja = tipPlacanja;
	}
	
}
