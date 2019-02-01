package com.ftn.paymentGateway.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.ftn.paymentGateway.enumerations.TransakcijaStatus;

@Entity
public class Transakcija {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private Long maticnaTransakcija;
	
	@Column(nullable = true)
	private String izvrsnaTransakcija;
	
	@Column(nullable = false)
	private double iznos;
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date vreme;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TransakcijaStatus status;
	
	@Column(nullable = false, length = 90, unique = true)
	private String jedinstveniToken;
	
	@Column(nullable = false)
	private boolean pretplata;
	
	@ManyToOne(optional = false)
	private EntitetPlacanja entitetPlacanja;
	
	@ManyToOne(optional = true)
	private TipPlacanja tipPlacanja;
	
	@Column(nullable = true)
    private String successURL;
    
	@Column(nullable = true)
    private String failedURL;
    
	@Column(nullable = true)
    private String errorURL;

	public Transakcija() {
		super();
	}

	public Transakcija(Long id, Long maticnaTransakcija, String izvrsnaTransakcija, double iznos, Date vreme,
			TransakcijaStatus status, String jedinstveniToken, boolean pretplata, EntitetPlacanja entitetPlacanja,
			TipPlacanja tipPlacanja, String successURL, String failedURL, String errorURL) {
		super();
		this.id = id;
		this.maticnaTransakcija = maticnaTransakcija;
		this.izvrsnaTransakcija = izvrsnaTransakcija;
		this.iznos = iznos;
		this.vreme = vreme;
		this.status = status;
		this.jedinstveniToken = jedinstveniToken;
		this.pretplata = pretplata;
		this.entitetPlacanja = entitetPlacanja;
		this.tipPlacanja = tipPlacanja;
		this.successURL = successURL;
		this.failedURL = failedURL;
		this.errorURL = errorURL;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMaticnaTransakcija() {
		return maticnaTransakcija;
	}

	public void setMaticnaTransakcija(Long maticnaTransakcija) {
		this.maticnaTransakcija = maticnaTransakcija;
	}

	public String getIzvrsnaTransakcija() {
		return izvrsnaTransakcija;
	}

	public void setIzvrsnaTransakcija(String izvrsnaTransakcija) {
		this.izvrsnaTransakcija = izvrsnaTransakcija;
	}

	public double getIznos() {
		return iznos;
	}

	public void setIznos(double iznos) {
		this.iznos = iznos;
	}

	public Date getVreme() {
		return vreme;
	}

	public void setVreme(Date vreme) {
		this.vreme = vreme;
	}

	public TransakcijaStatus getStatus() {
		return status;
	}

	public void setStatus(TransakcijaStatus status) {
		this.status = status;
	}

	public String getJedinstveniToken() {
		return jedinstveniToken;
	}

	public void setJedinstveniToken(String jedinstveniToken) {
		this.jedinstveniToken = jedinstveniToken;
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

	public boolean isPretplata() {
		return pretplata;
	}

	public void setPretplata(boolean pretplata) {
		this.pretplata = pretplata;
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
