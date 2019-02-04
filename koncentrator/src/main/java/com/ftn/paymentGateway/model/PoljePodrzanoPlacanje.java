package com.ftn.paymentGateway.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ftn.paymentGateway.enumerations.IdPoljePlacanja;

@Entity
public class PoljePodrzanoPlacanje {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private IdPoljePlacanja idPolja;
	
	@Column(nullable = true, length = 1200)
	private String vrednost;

	public PoljePodrzanoPlacanje() {
		super();
	}

	public PoljePodrzanoPlacanje(Long id, IdPoljePlacanja idPolja, String vrednost) {
		super();
		this.id = id;
		this.idPolja = idPolja;
		this.vrednost = vrednost;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public IdPoljePlacanja getIdPolja() {
		return idPolja;
	}

	public void setIdPolja(IdPoljePlacanja idPolja) {
		this.idPolja = idPolja;
	}

	public String getVrednost() {
		return vrednost;
	}

	public void setVrednost(String vrednost) {
		this.vrednost = vrednost;
	}
	
}
