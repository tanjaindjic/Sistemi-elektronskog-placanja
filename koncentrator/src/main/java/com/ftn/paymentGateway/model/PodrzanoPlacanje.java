package com.ftn.paymentGateway.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class PodrzanoPlacanje {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private boolean pretplata;
	
	@OneToMany
	@JsonBackReference
	private List<PoljePodrzanoPlacanje> polja;
	
	@ManyToOne(optional = true)
	private EntitetPlacanja entitetPlacanja;
	
	@ManyToOne(optional = true)
	private TipPlacanja tipPlacanja;

	public PodrzanoPlacanje() {
		super();
	}

	public PodrzanoPlacanje(Long id, boolean pretplata, List<PoljePodrzanoPlacanje> polja,
			EntitetPlacanja entitetPlacanja, TipPlacanja tipPlacanja) {
		super();
		this.id = id;
		this.pretplata = pretplata;
		this.polja = polja;
		this.entitetPlacanja = entitetPlacanja;
		this.tipPlacanja = tipPlacanja;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isPretplata() {
		return pretplata;
	}

	public void setPretplata(boolean pretplata) {
		this.pretplata = pretplata;
	}

	public List<PoljePodrzanoPlacanje> getPolja() {
		return polja;
	}

	public void setPolja(List<PoljePodrzanoPlacanje> polja) {
		this.polja = polja;
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
