package com.ftn.paymentGateway.dto;

import java.util.Date;

public class BankRequestDTO {
	
	private String merchantID;
	
    private String merchantPass;
    
    private Float iznos;
    
    private Long merchantOrderID;
    
    private Date merchantTimestamp;
    
    private String successURL;
    
    private String failedURL;
    
    private String errorURL;

}
