package com.ftn.paymentGateway.helpClasses;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class KPResponseDTO {
	
	@NotNull
	private Long merchantOrderID;
	
	@NotNull
	private Long acquirerOrderID;
	
	@NotNull
	private Date acquirerTimestamp;
	
	@NotNull
	private Long paymentID;
	
	@NotNull
	private String status;

	public KPResponseDTO() {
		super();
	}

	public KPResponseDTO(@NotNull Long merchantOrderID, @NotNull Long acquirerOrderID, @NotNull Date acquirerTimestamp,
			@NotNull Long paymentID, @NotNull String status) {
		super();
		this.merchantOrderID = merchantOrderID;
		this.acquirerOrderID = acquirerOrderID;
		this.acquirerTimestamp = acquirerTimestamp;
		this.paymentID = paymentID;
		this.status = status;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "KPResponseDTO [merchantOrderID=" + merchantOrderID + ", acquirerOrderID=" + acquirerOrderID
				+ ", acquirerTimestamp=" + acquirerTimestamp + ", paymentID=" + paymentID + ", status=" + status + "]";
	}

}
