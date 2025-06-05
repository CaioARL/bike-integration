package br.edu.ifsp.spo.bike_integration.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.model.Token;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import br.edu.ifsp.spo.bike_integration.repository.TokenRepository;
import br.edu.ifsp.spo.bike_integration.response.ValidateTokenResponse;

@Service
public class TokenService {

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private UsuarioService usuarioService;

	public Token generateToken(String email) {
		Token lastToken = tokenRepository.findLastTokenByEmail(email).orElse(null);

		if (lastToken != null) {
			this.disableToken(lastToken);
		}
		return tokenRepository.save(
				Token.builder().dtCriacao(LocalDateTime.now())
						.dtExpiracao(LocalDateTime.now().plusMinutes(6)).tokenGerado(this.generateToken()).email(email)
						.tokenGerado(this.generateToken()).email(email).build());
	}

	public Token getToken(String tokenValue) {
		return this.tokenRepository.findByTokenGerado(tokenValue).orElse(null);
	}

	public ValidateTokenResponse validateTokenForRecover(String tokenValue, String email) {
		ValidateTokenResponse response = ValidateTokenResponse.builder().success(false).token(null).build();
		if (this.isValidToken(tokenValue, email)) {
			response.setSuccess(true);
			Usuario usuario = usuarioService.refreshSession(email);
			if (usuario != null) {
				response.setToken(usuario.getSessao().getToken());
			}
		}
		return response;
	}

	public ValidateTokenResponse validateTokenForRegister(String tokenValue, String email) {
		ValidateTokenResponse response = ValidateTokenResponse.builder().success(false).token(null).build();
		if (this.isValidToken(tokenValue, email)) {
			response.setSuccess(true);
		}
		return response;
	}

	public Boolean isValidToken(String tokenValue, String email) {
		Token token = this.getToken(tokenValue);
		if (token != null && email.equals(token.getEmail())
				&& token.getDtExpiracao().isAfter(LocalDateTime.now().plusMinutes(3))) {
			this.disableToken(token);
			return true;
		}
		return false;
	}

	public List<Token> listTokens() {
		return tokenRepository.findAll();
	}

	public List<Token> listTokensByEmail(String email) {
		return tokenRepository.findByEmail(email);
	}

	public List<Token> listAllExpiredTokens() {
		return tokenRepository.findAllExpiredTokens();
	}

	public Token getLastTokenByEmail(String email) {
		return tokenRepository.findLastTokenByEmail(email).orElse(null);
	}

	public Token disableToken(Token token) {
		token.setDtExpiracao(LocalDateTime.now().minusMinutes(1));
		return tokenRepository.save(token);
	}

	public void removeToken(Token token) {
		tokenRepository.delete(token);
	}

	public void removeAll(List<Token> tokens) {
		tokenRepository.deleteAll(tokens);
	}

	/*
	 * PRIVATE METHODS
	 */

	// Gera toke de 4 digitos
	private String generateToken() {
		return UUID.randomUUID().toString().substring(0, 4);
	}

}
