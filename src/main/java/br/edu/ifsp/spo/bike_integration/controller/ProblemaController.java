package br.edu.ifsp.spo.bike_integration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.dto.JwtUserDTO;
import br.edu.ifsp.spo.bike_integration.dto.ProblemaDTO;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.Problema;
import br.edu.ifsp.spo.bike_integration.service.ProblemaService;
import br.edu.ifsp.spo.bike_integration.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/problema")
@Tag(name = "Problema", description = "Controller para operações relacionadas a problemas.")
public class ProblemaController {

	@Autowired
	private ProblemaService problemaService;

	@Role(RoleType.PF)
	@BearerToken
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Retorna um problema pelo ID.")
	public ResponseEntity<Problema> getProblema(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(problemaService.loadProblemaById(id));
	}

	@Role(RoleType.PF)
	@BearerToken
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Registra um problema.")
	public ResponseEntity<Problema> create(@RequestBody ProblemaDTO problema) {
		return ResponseEntity.status(HttpStatus.CREATED).body(problemaService.create(problema));
	}

	@Role(RoleType.PF)
	@BearerToken
	@GetMapping(path = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Checar a possibilidade de criação de problema em um trecho.")
	public ResponseEntity<Boolean> existsTrechoByLatitudeAndLongitude(@RequestParam Double latitude,
			@RequestParam Double longitude) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(problemaService.existsTrechoByLatitudeAndLongitude(latitude, longitude));
	}

	@Role(RoleType.PF)
	@BearerToken
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Atualiza um problema.")
	public ResponseEntity<Problema> update(@PathVariable Long id, @RequestBody ProblemaDTO problema) {
		return ResponseEntity.status(HttpStatus.OK).body(problemaService.updateProblema(id, problema));
	}

	@Role(RoleType.PF)
	@BearerToken
	@PutMapping(path = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Atualiza a foto de um problema.")
	public void updateFotoProblema(@PathVariable Long id, @RequestParam MultipartFile file) {
		if (!FileUtils.isValidFileType(file))
			throw new IllegalArgumentException("Formato de arquivo inválido. Aceito apenas JPEG.");
		problemaService.updateFotoProblema(id, file);
	}

	@Role(RoleType.PF)
	@BearerToken
	@DeleteMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Deleta um problema.")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		problemaService.delete(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Role(RoleType.PF)
	@BearerToken
	@PostMapping(path = "/{id}/report", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Reporta um problema como não existente.")
	public ResponseEntity<Void> reportProblem(@AuthenticationPrincipal JwtUserDTO jwtUserDTO, @PathVariable Long id,
			@RequestParam boolean exists) {
		problemaService.reportProblem(id, jwtUserDTO, exists);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Role(RoleType.PF)
	@BearerToken
	@GetMapping(path = "/radius", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Busca problemas em um raio estipulado com base na latitude e longitude informadas.")
	public ResponseEntity<List<Problema>> buscarEventosByRadius(
			@RequestParam(required = true) Double latitude,
			@RequestParam(required = true) Double longitude,
			@RequestParam(required = true) Double raio) {
		return ResponseEntity.ok(problemaService.buscarProblemasByRadius(latitude, longitude, raio));
	}
}
