package com.ftn.paymentGateway.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.ftn.paymentGateway.enumerations.TransakcijaStatus;

public class BankResponseDTO {

	private TransakcijaStatus statusTransakcije;

	private Long merchantOrderID; //ono sto je poslao prodavac u prvom zahtevu

	private Long acquirerOrderID; //isto kao gore ako je ista banka ili drugi id od kreirane druge transakcije

	private Date acquirerTimestamp; //-||-

	private Long paymentID; //id pocetne transakcije

	private String redirectURL; //ili je success ili failed url

	public BankResponseDTO() {
		super();
	}

	public BankResponseDTO(@NotNull Long merchantOrderID, @NotNull Long acquirerOrderID,
			@NotNull Date acquirerTimestamp, @NotNull Long paymentID, @NotNull TransakcijaStatus status,
			@NotNull String redirectURL) {
		super();
		this.merchantOrderID = merchantOrderID;
		this.acquirerOrderID = acquirerOrderID;
		this.acquirerTimestamp = acquirerTimestamp;
		this.paymentID = paymentID;
		this.statusTransakcije = status;
		this.redirectURL = redirectURL;
	}

	public Long getMerchantOrderID() {
		return merchantOrderID;
	}

	public void setMerchantOrderID(Long merchantOrderID) {
		this.merchantOrderID = merchantOrderID;
	}

	public Long getAcquirerOrderID() {
		return acquirerOrderID;
	}

	public void setAcquirerOrderID(Long acquirerOrderID) {
		this.acquirerOrderID = acquirerOrderID;
	}

	public Date getAcquirerTimestamp() {
		return acquirerTimestamp;
	}

	public void setAcquirerTimestamp(Date acquirerTimestamp) {
		this.acquirerTimestamp = acquirerTimestamp;
	}

	public Long getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(Long paymentID) {
		this.paymentID = paymentID;
	}

	public TransakcijaStatus getStatus() {
		return statusTransakcije;
	}

	public void setStatus(TransakcijaStatus status) {
		this.statusTransakcije = status;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	@Override
	public String toString() {
		return "BankResponseDTO [merchantOrderID=" + merchantOrderID + ", acquirerOrderID=" + acquirerOrderID
				+ ", acquirerTimestamp=" + acquirerTimestamp + ", paymentID=" + paymentID + ", status=" + statusTransakcije + "]";
	}

}
