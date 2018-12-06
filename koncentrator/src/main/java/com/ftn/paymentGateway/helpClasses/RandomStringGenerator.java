package com.ftn.paymentGateway.helpClasses;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class RandomStringGenerator {

	public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	public static SecureRandom rnd = new SecureRandom();

	public RandomStringGenerator() {
		
	}
	
	public String genRandomString(int len){
	
		StringBuilder sb = new StringBuilder(len);
		for( int i = 0; i < len; i++ ) 
		   sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}
	
}
