package com.ftn.paymentGateway.dto;

import java.util.Date;

import com.ftn.paymentGateway.enumerations.TransakcijaStatus;

public class BankResponseDTO {
	
	private Long merchantOrderID;
	
	private Long acquirerOrderID;
	
	private Date acquirerTimestamp;
	
	private Long paymentID;
	
	private TransakcijaStatus status;

}
