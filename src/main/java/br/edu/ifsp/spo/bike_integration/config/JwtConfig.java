package br.edu.ifsp.spo.bike_integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.edu.ifsp.spo.bike_integration.model.dto.JwtConfigDTO;

@Configuration
public class JwtConfig {

	@Value("${jwt.access-key}")
	private String accessKey;

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private Long expiration;

	@Bean
	JwtConfigDTO jwtConfigDto() {
		return JwtConfigDTO.builder().accessKey(this.accessKey).secretKey(this.secretKey)
				.expiration(this.expiration * 1000).build();
	}

}