package pl.aw84.imagelib.imageapi;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication
public class ImageApiApplication {

	public static void main(String[] args) {
		Security.setProperty("crypto.policy", "unlimited");
		Security.addProvider(new BouncyCastleProvider());
		SpringApplication.run(ImageApiApplication.class, args);
	}

	@Bean
	WebClient imageApiWebClient() {
		return WebClient.create();
	}

}
