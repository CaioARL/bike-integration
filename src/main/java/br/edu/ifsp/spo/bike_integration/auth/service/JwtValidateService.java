package br.edu.ifsp.spo.bike_integration.auth.service;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import br.edu.ifsp.spo.bike_integration.dto.JwtConfigDTO;
import br.edu.ifsp.spo.bike_integration.dto.JwtUserDTO;
import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import br.edu.ifsp.spo.bike_integration.service.SessaoService;
import br.edu.ifsp.spo.bike_integration.util.ObjectMapperUtils;

@Service
public class JwtValidateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtValidateService.class);

	public static final String DEFAULT_NULL_TOKEN_MESSAGE = "Token inválido ou expirado";

	@Autowired
	private JwtConfigDTO config;

	@Autowired
	private SessaoService sessaoService;

	public String create(JwtUserDTO subject) throws BikeIntegrationCustomException {
		return JWT.create().withSubject(ObjectMapperUtils.toJsonString(subject)).withIssuedAt(new Date())
				.withExpiresAt(new Date(System.currentTimeMillis() + (this.config.getExpiration())))
				.sign(this.createAlgorithm());
	}

	public boolean isAuthenticated(String token, RoleType... roles) {
		try {
			JwtUserDTO subject = this.getSubject(token);
			return subject != null
					&& StringUtils.isNotBlank(subject.getEmail())
					&& StringUtils.isNotBlank(subject.getNickname())
					&& ArrayUtils.isNotEmpty(roles)
					&& this.isValidRole(token, roles);
		} catch (Exception e) {
			LOGGER.debug("Error on isAuthenticated: ", e);
			return false;
		}
	}

	public JwtUserDTO getSubject(String token) {
		try {
			return ObjectMapperUtils.toPojo(this.createDecoded(token).getSubject(), JwtUserDTO.class);
		} catch (Exception e) {
			LOGGER.debug("Error on getSubject: ", e);
			return null;
		}
	}

	/**
	 *
	 * PRIVATE's
	 *
	 *
	 */
	private boolean isValidRole(String token, RoleType... roles) {
		Usuario usuario = this.sessaoService.getUsuarioByToken(token);
		return usuario != null && roles.length > 0
				&& (ArrayUtils.contains(roles, usuario.getRole()) || usuario.getRole().equals(RoleType.ADMIN));
	}

	private DecodedJWT createDecoded(String token) throws JWTVerificationException {
		return this.createVerifier().verify(token);
	}

	private JWTVerifier createVerifier() throws BikeIntegrationCustomException {
		return JWT.require(this.createAlgorithm()).build();
	}

	private Algorithm createAlgorithm() throws BikeIntegrationCustomException {
		return Algorithm.HMAC512(this.config.getSFecretKey().getBytes());
	}

}