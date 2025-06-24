package br.edu.ifsp.spo.bike_integration.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.netty.handler.codec.http.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

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

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable)
				.cors(customizer -> customizer.configurationSource(this.corsConfigurationSource()))
				.authorizeHttpRequests(customizer -> customizer.anyRequest().permitAll())
				.formLogin(AbstractHttpConfigurer::disable)
				.sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers(getPublicRequestMatchersAsArray());
	}

	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList(this.corsAllowedOriginPatterns.split(",")));
		configuration.setAllowedMethods(Arrays.asList(this.corsAllowedMethods.split(",")));
		configuration.setAllowedHeaders(Arrays.asList(this.corsAllowedHeaders.split(",")));
		configuration.setAllowCredentials(this.corsAllowCredentials);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(this.corsMapping, configuration);
		return source;
	}

	public static boolean isPublic(HttpServletRequest request) {
		List<RequestMatcher> matchers = getPublicRequestMatchers();
		for (RequestMatcher matcher : matchers) {
			if (matcher.matches(request)) {
				return true;
			}
		}
		return false;
	}

	public static RequestMatcher[] getPublicRequestMatchersAsArray() {
		List<RequestMatcher> matchers = getPublicRequestMatchers();
		return matchers.toArray(RequestMatcher[]::new);
	}

	public static List<RequestMatcher> getPublicRequestMatchers() {
		List<RequestMatcher> matchers = new ArrayList<>();
		matchers.add(EndpointRequest.toAnyEndpoint());
		matchers.add(new AntPathRequestMatcher("/actuator/**", null));
		matchers.add(new AntPathRequestMatcher("/error", null));
		matchers.add(new RegexRequestMatcher("/v3/api-docs.*", null));
		matchers.add(new RegexRequestMatcher("/api-docs.*", null));
		matchers.add(new RegexRequestMatcher("/swagger.*", null));
		matchers.add(new RegexRequestMatcher("/v1/app.*", HttpMethod.GET.name()));
		matchers.add(new AntPathRequestMatcher("/v1/auth", HttpMethod.POST.name()));
		matchers.add(new AntPathRequestMatcher("/ws/**", null));
		return matchers;
	}

}