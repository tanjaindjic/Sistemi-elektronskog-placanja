package com.ftn.paymentGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaymentGatewayApplication {

	public static void main(String[] args) {
		
		System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jre1.8.0_191\\lib\\security\\cacerts");
		SpringApplication.run(PaymentGatewayApplication.class, args);
	}
}
