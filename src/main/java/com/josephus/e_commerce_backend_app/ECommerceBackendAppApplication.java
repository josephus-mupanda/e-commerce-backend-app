package com.josephus.e_commerce_backend_app;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ECommerceBackendAppApplication {
	public static void main(String[] args) {
//        Dotenv dotenv = Dotenv.load();
        // Load .env file and set as system properties
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Manually set each required variable as system property
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        SpringApplication.run(ECommerceBackendAppApplication.class, args);
	}
}
