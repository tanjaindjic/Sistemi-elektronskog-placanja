package sep.tim18.banka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankaApplication {

	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_191\\jre\\lib\\security\\cacerts");
		SpringApplication.run(BankaApplication.class, args);
	}
}
