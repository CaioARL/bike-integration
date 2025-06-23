package br.edu.ifsp.spo.bike_integration.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD).setFieldMatchingEnabled(true)
				.setFieldAccessLevel(AccessLevel.PRIVATE).setSkipNullEnabled(true);
		return modelMapper;
	}

}