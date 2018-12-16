package com.ftn.paymentGateway.helpClasses;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class KPRequestDTO {
	
	@NotNull
	private String merchantID;
	
	@NotNull
    private String merchantPass;
    
	@NotNull
    private Float iznos;
    
	@NotNull
    private Long merchantOrderID;
    
	@NotNull
    private Date merchantTimestamp;
    
	@NotNull
    private String successURL;
    
	@NotNull
    private String failedURL;
    
	@NotNull
    private String errorURL;

	public KPRequestDTO() {
		super();
	}

	public KPRequestDTO(@NotNull String merchantID, @NotNull String merchantPass, @NotNull Float iznos,
			@NotNull Long merchantOrderID, @NotNull Date merchantTimestamp, @NotNull String successURL,
			@NotNull String failedURL, @NotNull String errorURL) {
		super();
		this.merchantID = merchantID;
		this.merchantPass = merchantPass;
		this.iznos = iznos;
		this.merchantOrderID = merchantOrderID;
		this.merchantTimestamp = merchantTimestamp;
		this.successURL = successURL;
		this.failedURL = failedURL;
		this.errorURL = errorURL;
	}

	public String getMerchantID() {
		return merchantID;
	}

	public void setMerchantID(String merchantID) {
		this.merchantID = merchantID;
	}

	public String getMerchantPass() {
		return merchantPass;
	}

	public void setMerchantPass(String merchantPass) {
		this.merchantPass = merchantPass;
	}

	public Float getIznos() {
		return iznos;
	}

	public void setIznos(Float iznos) {
		this.iznos = iznos;
	}

	public Long getMerchantOrderID() {
		return merchantOrderID;
	}

	public void setMerchantOrderID(Long merchantOrderID) {
		this.merchantOrderID = merchantOrderID;
	}

	public Date getMerchantTimestamp() {
		return merchantTimestamp;
	}

	public void setMerchantTimestamp(Date merchantTimestamp) {
		this.merchantTimestamp = merchantTimestamp;
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

	@Override
	public String toString() {
		return "KPRequestDTO [merchantID=" + merchantID + ", merchantPass=" + merchantPass + ", iznos=" + iznos
				+ ", merchantOrderID=" + merchantOrderID + ", merchantTimestamp=" + merchantTimestamp + ", successURL="
				+ successURL + ", failedURL=" + failedURL + ", errorURL=" + errorURL + "]";
	}
	
	
	
}
