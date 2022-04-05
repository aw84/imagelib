package pl.aw84.imagemgr.imgapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageLibApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageLibApplication.class, args);
		try {
			throw new NullPointerException();

		} catch (RuntimeException e) {

			System.err.println("Cougth NULL POINTER");
		}
	}

}