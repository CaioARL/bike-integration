package br.edu.ifsp.spo.bike_integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.edu.ifsp.spo.bike_integration.interceptor.AuthenticationInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${app.cors.mapping:/**}")
	private String corsMapping;

	@Value("${app.cors.allowedOriginPatterns:*}")
	private String corsAllowedOriginPatterns;

	@Value("${app.cors.allowedMethods:GET,POST,DELETE,PUT,PATCH,OPTIONS,HEAD}")
	private String corsAllowedMethods;

	@Value("${app.cors.allowedHeaders:*}")
	private String corsAllowedHeaders;

	@Value("${app.cors.allowCredentials:true}")
	private boolean corsAllowCredentials;

	private final AuthenticationInterceptor authenticationInterceptor;

	public WebMvcConfig(final AuthenticationInterceptor authenticationInterceptor) {
		this.authenticationInterceptor = authenticationInterceptor;
	}

	@Override
	public void addCorsMappings(@NonNull CorsRegistry registry) {
		registry.addMapping(this.corsMapping).allowedOriginPatterns(this.corsAllowedOriginPatterns.split(","))
				.allowedMethods(this.corsAllowedMethods.split(",")).allowedHeaders(this.corsAllowedHeaders.split(","))
				.allowCredentials(this.corsAllowCredentials);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor);
	}

}