package com.ftn.paymentGateway.helpClasses;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ftn.paymentGateway.PaymentGatewayApplication;


public class RSAEncryptDecrypt {
	
   public static String decrypt(String cipherText) throws Exception {
	 //  System.out.println("radim dekriptovanje od "+cipherText);
		KeyPair pair = getKeyPairFromKeyStore();
		PrivateKey privateKey = pair.getPrivate();
        byte[] bytes = Base64.getDecoder().decode(cipherText);

        Cipher decriptCipher = Cipher.getInstance("RSA");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);
	  // System.out.println("...................."+new String(decriptCipher.doFinal(bytes), UTF_8));

        return new String(decriptCipher.doFinal(bytes), UTF_8);
    }
	
	public static String encrypt(String plainText) throws Exception {
		//System.out.println("radim kriptovanje od "+plainText);
		KeyPair pair = getKeyPairFromKeyStore();
		PublicKey publicKey = pair.getPublic();
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
      //  System.out.println("...................."+Base64.getEncoder().encodeToString(cipherText));
        return Base64.getEncoder().encodeToString(cipherText);
    }
	private static KeyPair getKeyPairFromKeyStore() throws Exception {
	    InputStream ins = PaymentGatewayApplication.class.getResourceAsStream("/payment-gateway.jks");

	    KeyStore keyStore = KeyStore.getInstance("jks");
	    keyStore.load(ins, "paymentpass".toCharArray());   //Keystore password
	    KeyStore.PasswordProtection keyPassword =       //Key password
	            new KeyStore.PasswordProtection("paymentpass".toCharArray());

	    KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("payment-gateway", keyPassword);

	    java.security.cert.Certificate cert = keyStore.getCertificate("payment-gateway");
	    PublicKey publicKey = cert.getPublicKey();
	    PrivateKey privateKey = privateKeyEntry.getPrivateKey();

	    return new KeyPair(publicKey, privateKey);
	}
}
