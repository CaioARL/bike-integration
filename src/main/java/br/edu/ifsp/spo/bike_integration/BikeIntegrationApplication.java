package br.edu.ifsp.spo.bike_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BikeIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BikeIntegrationApplication.class, args);
	}

}
