package br.edu.ifsp.spo.bike_integration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.annotation.XAccessKey;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.NivelHabilidade;
import br.edu.ifsp.spo.bike_integration.model.dto.NivelHabilidadeDTO;
import br.edu.ifsp.spo.bike_integration.service.NivelHabilidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/nivel/habilidade")
@Tag(name = "Nivel Habilidade", description = "Controller para operações relacionadas a níveis de habilidade.")
public class NivelHabilidadeController {

	@Autowired
	private NivelHabilidadeService nivelHabilidadeService;

	@Role({ RoleType.PF, RoleType.PJ, RoleType.ADMIN })
	@BearerToken
	@XAccessKey
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Retorna todos os níveis de habilidade cadastrados.")
	public List<NivelHabilidade> listarNiveisHabilidade() {
		return nivelHabilidadeService.listarNiveisHabilidade();
	}

	@Role(RoleType.ADMIN)
	@BearerToken
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Cria um novo nível de habilidade.")
	public ResponseEntity<NivelHabilidade> cadastrarNivelHabilidade(@RequestBody NivelHabilidadeDTO nivelHabilidade) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(nivelHabilidadeService.cadastrarNivelHabilidade(nivelHabilidade));
	}
}
