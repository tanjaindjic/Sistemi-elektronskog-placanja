package com.ftn.paymentGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaymentGatewayApplication {

	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk-11.0.1\\lib\\security\\cacerts");
	//	System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_191\\jre\\lib\\security\\cacerts");
		SpringApplication.run(PaymentGatewayApplication.class, args);
	}
}
