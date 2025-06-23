package br.edu.ifsp.spo.bike_integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidatorConfig {

	@Bean
	LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}
}
