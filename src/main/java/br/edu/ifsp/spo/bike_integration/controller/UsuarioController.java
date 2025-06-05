package br.edu.ifsp.spo.bike_integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.annotation.XAccessKey;
import br.edu.ifsp.spo.bike_integration.dto.JwtUserDTO;
import br.edu.ifsp.spo.bike_integration.dto.UsuarioAdmDTO;
import br.edu.ifsp.spo.bike_integration.dto.UsuarioDTO;
import br.edu.ifsp.spo.bike_integration.dto.UsuarioUpdateDTO;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import br.edu.ifsp.spo.bike_integration.service.EventoService;
import br.edu.ifsp.spo.bike_integration.service.UsuarioService;
import br.edu.ifsp.spo.bike_integration.util.FileUtils;
import br.edu.ifsp.spo.bike_integration.util.validate.CpfValidate.CpfValidationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/usuario")
@Tag(name = "Usuário", description = "Controller para operações relacionadas a usuários.")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EventoService eventoService;

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Retorna os detalhes de um usuário pelo ID informado.")
	public ResponseEntity<Usuario> get(@AuthenticationPrincipal JwtUserDTO jwtUserDTO) {
		return ResponseEntity.ok(usuarioService.loadUsuarioByJwt(jwtUserDTO));
	}

	@Role(RoleType.ADMIN)
	@XAccessKey
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Cria um novo usuário.")
	public ResponseEntity<Usuario> create(@RequestBody UsuarioDTO usuarioDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.createUsuario(usuarioDto));
	}

	@Role(RoleType.ADMIN)
	@XAccessKey
	@PostMapping(path = "/admin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Cria um novo usuário administrador.")
	public ResponseEntity<Usuario> createAdm(@RequestBody UsuarioAdmDTO usuarioAdmDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.createUsuarioAdm(usuarioAdmDto));
	}

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Atualiza os dados de um usuário existente.")
	public ResponseEntity<Usuario> update(@AuthenticationPrincipal JwtUserDTO jwtUserDTO,
			@RequestBody UsuarioUpdateDTO usuarioDto) {
		return ResponseEntity
				.ok(usuarioService.updateUsuario(usuarioService.loadUsuarioByJwt(jwtUserDTO).getId(), usuarioDto));
	}

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@PutMapping(path = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Atualiza a foto do usuário.")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarFotoUsuario(@AuthenticationPrincipal JwtUserDTO jwtUserDTO, @RequestParam MultipartFile file) {
		if (!FileUtils.isValidFileType(file))
			throw new IllegalArgumentException("Formato de arquivo inválido. Aceito apenas JPEG.");
		usuarioService.updateFotoUsuario(usuarioService.loadUsuarioByJwt(jwtUserDTO).getId(), file);
	}

	@Role(RoleType.ADMIN)
	@BearerToken
	@DeleteMapping
	@Operation(summary = "Remove um usuário e seus eventos associados pelo ID informado.")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@AuthenticationPrincipal JwtUserDTO jwtUserDTO) {
		String id = usuarioService.loadUsuarioByJwt(jwtUserDTO).getId();
		eventoService.deleteEventosByUsuario(id);
		usuarioService.deleteUsuario(id);
	}

	@Role({ RoleType.PF, RoleType.ADMIN })
	@BearerToken
	@XAccessKey
	@GetMapping(path = "/cpf/validate", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Valida e formata um CPF informado.")
	public ResponseEntity<CpfValidationResult> validateCpf(@RequestParam String cpf) {
		return ResponseEntity.ok(usuarioService.validateCpf(cpf));
	}

	@Role({ RoleType.PF, RoleType.ADMIN })
	@BearerToken
	@XAccessKey
	@GetMapping(path = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Valida se o email ou nome de usuário já estão cadastrados.")
	public ResponseEntity<Boolean> validateEmail(
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String nomeUsuario) {
		if (email == null && nomeUsuario == null) {
			throw new IllegalArgumentException(
					"Pelo menos um dos parâmetros (email ou nomeUsuario) deve ser informado.");
		}
		return ResponseEntity.ok(usuarioService.validateUsuarioByEmailOrNomeUsuario(nomeUsuario, email));
	}

	@Role({ RoleType.PF, RoleType.ADMIN })
	@BearerToken
	@XAccessKey
	@GetMapping(path = "/cnpj/validate")
	@Operation(summary = "Valida se o CNPJ já está cadastrado.")
	public ResponseEntity<Boolean> validateCNPJ(@RequestParam(required = true) String cnpj) {
		return ResponseEntity.ok(usuarioService.validateUsuarioByCnpj(cnpj));
	}
}