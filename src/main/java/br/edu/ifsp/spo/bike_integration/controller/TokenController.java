package br.edu.ifsp.spo.bike_integration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.annotation.XAccessKey;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.Token;
import br.edu.ifsp.spo.bike_integration.response.ValidateTokenResponse;
import br.edu.ifsp.spo.bike_integration.service.EmailService;
import br.edu.ifsp.spo.bike_integration.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("v1/token")
@Tag(name = "Token", description = "Controller para operações relacionadas a token.")
public class TokenController {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private EmailService emailService;

	@Role(RoleType.ADMIN)
	@XAccessKey
	@GetMapping(path = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Verifica se o token é válido para o email informado.")
	public ResponseEntity<ValidateTokenResponse> isValidateToken(@RequestParam String token,
			@RequestParam String email) {
		return ResponseEntity.status(HttpStatus.OK).body(tokenService.validateTokenForRegister(token, email));
	}

	@Role(RoleType.ADMIN)
	@XAccessKey
	@PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Gera um novo token e envia por email para o usuário.")
	@ResponseStatus(HttpStatus.OK)
	public void sendTokenEmail(@RequestParam String email) throws MessagingException {
		emailService.sendCadastroTokenEmail(email, tokenService.generateToken(email).getTokenGerado());
	}

	@Role(RoleType.ADMIN)
	@XAccessKey
	@PostMapping(path = "/recover", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Gera um novo token de recuperacao e envia por email para o usuário.")
	@ResponseStatus(HttpStatus.OK)
	public void sendRecoveryTokenEmail(@RequestParam String email) throws MessagingException {
		emailService.sendRecuperacaoTokenEmail(email, tokenService.generateToken(email).getTokenGerado());
	}

	@Role(RoleType.ADMIN)
	@XAccessKey
	@GetMapping(path = "recover/validate", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Verifica se o token é válido para o email informado.")
	public ResponseEntity<ValidateTokenResponse> isValidateRecoveryToken(@RequestParam String token,
			@RequestParam String email) {
		return ResponseEntity.status(HttpStatus.OK).body(tokenService.validateTokenForRecover(token, email));
	}

	@Role(RoleType.ADMIN)
	@XAccessKey
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Recupera a lista de tokens associados ao email do usuário.")
	public ResponseEntity<List<Token>> listTokensByUser(@RequestParam String email) {
		return ResponseEntity.status(HttpStatus.OK).body(tokenService.listTokensByEmail(email));
	}
}