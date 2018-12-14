package com.ftn.paymentGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class PaymentGatewayApplication {

	public static void main(String[] args) {
		
		System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk-11.0.1\\lib\\security\\cacerts");
		SpringApplication.run(PaymentGatewayApplication.class, args);
	}
}
