package br.edu.ifsp.spo.bike_integration.interceptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.annotation.XAccessKey;
import br.edu.ifsp.spo.bike_integration.config.WebSecurityConfig;
import br.edu.ifsp.spo.bike_integration.exception.CryptoException;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.dto.StandardErrorDTO;
import br.edu.ifsp.spo.bike_integration.service.auth.AccessKeyValidateService;
import br.edu.ifsp.spo.bike_integration.service.auth.JwtValidateService;
import br.edu.ifsp.spo.bike_integration.util.RequestUtils;
import br.edu.ifsp.spo.bike_integration.util.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

	@Autowired
	private JwtValidateService jwtValidateService;

	@Autowired
	private AccessKeyValidateService accessKeyValidateService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException, CryptoException {
		if (WebSecurityConfig.isPublic(request)) {
			return true;
		}

		if (handler instanceof HandlerMethod method) {
			RoleType[] roles = method.getMethodAnnotation(Role.class).value();

			boolean isBearer = method.hasMethodAnnotation(BearerToken.class)
					&& RequestUtils.getBearerToken(request).isPresent()
					&& this.authenticateWithBearer(request, response, roles);
			boolean isAccessKey = method.hasMethodAnnotation(XAccessKey.class)
					&& RequestUtils.getAccessKey(request).isPresent()
					&& this.authenticateWithAccessKey(request, response, roles);

			if (!isBearer && !isAccessKey) {
				this.putErrorOnResponse(response);
				return false;
			}
		}
		return true;
	}

	/*
	 * PRIVATE METHODS
	 */
	private boolean authenticateWithBearer(HttpServletRequest request, HttpServletResponse response, RoleType[] roles)
			throws IOException, CryptoException {
		Optional<String> optToken = RequestUtils.getBearerToken(request);
		if (optToken.isEmpty() || !this.jwtValidateService.isAuthenticated(optToken.get(), roles)) {
			return false;
		}
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(this.jwtValidateService.getSubject(optToken.get()), null,
						new HashSet<>()));
		return true;
	}

	private boolean authenticateWithAccessKey(HttpServletRequest request, HttpServletResponse response,
			RoleType[] roles)
			throws IOException {
		Optional<String> optSecretKey = RequestUtils.getAccessKey(request);
		if (optSecretKey.isEmpty() || !this.accessKeyValidateService.isValid(optSecretKey.get(), roles)) {
			return false;
		}
		return true;
	}

	private void putErrorOnResponse(HttpServletResponse response) {
		this.putErrorOnResponse(response, JwtValidateService.DEFAULT_NULL_TOKEN_MESSAGE);
	}

	private void putErrorOnResponse(HttpServletResponse response, String message) {
		HttpStatus status = HttpStatus.FORBIDDEN;
		ResponseUtils.putJsonOnResponse(response, StandardErrorDTO.builder().status(status.value())
				.message(message).build(), status);
	}
}