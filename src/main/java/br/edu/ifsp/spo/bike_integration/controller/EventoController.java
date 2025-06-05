package br.edu.ifsp.spo.bike_integration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.dto.EventoDTO;
import br.edu.ifsp.spo.bike_integration.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.dto.JwtUserDTO;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.Evento;
import br.edu.ifsp.spo.bike_integration.response.ListEventoResponse;
import br.edu.ifsp.spo.bike_integration.service.EventoService;
import br.edu.ifsp.spo.bike_integration.service.UsuarioService;
import br.edu.ifsp.spo.bike_integration.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/evento")
@Tag(name = "Evento", description = "Controller para operações relacionadas a eventos.")
public class EventoController {

	@Autowired
	private EventoService eventoService;

	@Autowired
	private UsuarioService usuarioService;

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Lista todos os eventos com paginação e filtros.")
	public ResponseEntity<ListEventoResponse> listarEventos(
			@RequestParam(required = true) Long pagina,
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) String descricao,
			@RequestParam(required = false) String data,
			@RequestParam(required = false) String cidade,
			@RequestParam(required = false) String estado,
			@RequestParam(required = false) Long faixaKm,
			@RequestParam(required = false) Long tipoEvento,
			@RequestParam(required = false) Long nivelHabilidade,
			@RequestParam(required = false) Boolean gratuito,
			@RequestParam(required = false) Boolean aprovado) {
		return ResponseEntity.ok(eventoService.listarEventos(pagina, nome, descricao, data, cidade, estado, faixaKm,
				tipoEvento, nivelHabilidade, gratuito, aprovado, null));
	}

	@Role(RoleType.PJ)
	@BearerToken
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Cria um novo evento.")
	public ResponseEntity<Evento> cadastrarEvento(@RequestBody EventoDTO evento) {
		return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.createEvento(evento));
	}

	@Role(RoleType.PJ)
	@BearerToken
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Atualiza um evento pelo ID.")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarEvento(@PathVariable("id") Long id, @RequestBody EventoDTO evento) {
		eventoService.updateEvento(id, evento);
	}

	@Role(RoleType.PJ)
	@BearerToken
	@PutMapping(path = "/{id}/{aprovar}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Aprova um evento pelo ID.")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void aprovarEvento(@PathVariable("id") Long id, @PathVariable("aprovar") Boolean aprovar) {
		eventoService.aprovarEvento(id, aprovar != null ? aprovar : false);
	}

	@Role(RoleType.PJ)
	@BearerToken
	@PutMapping(path = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Atualiza a foto do evento.")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarFotoEvento(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
		if (!FileUtils.isValidFileType(file))
			throw new IllegalArgumentException("Formato de arquivo inválido. Aceito apenas JPEG.");
		eventoService.updateFotoEvento(id, file);
	}

	@Role(RoleType.ADMIN)
	@BearerToken
	@DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Deleta um evento pelo ID.")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletarEvento(@PathVariable("id") Long id) {
		eventoService.deleteEvento(id);
	}

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Busca um evento pelo ID.")
	public ResponseEntity<Evento> buscarEvento(@PathVariable("id") Long id) {
		return ResponseEntity.ok(eventoService.buscarEvento(id));
	}

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@GetMapping(path = "/{id}/geojson", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Busca um evento pelo ID e retorna no formato GeoJson.")
	public ResponseEntity<GeoJsonDTO> buscarEventoAsGeoJson(@PathVariable("id") Long id) throws NotFoundException {
		return ResponseEntity.ok(eventoService.buscarEventoAsGeoJsonById(id));
	}

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@GetMapping(path = "/radius", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Busca eventos em um raio estipulado com base na latitude e longitude informadas.")
	public ResponseEntity<List<Evento>> buscarEventosByRadius(
			@RequestParam(required = true) Double latitude,
			@RequestParam(required = true) Double longitude,
			@RequestParam(required = true) Double raio) {
		return ResponseEntity.ok(eventoService.buscarEventosByRadius(latitude, longitude, raio));
	}

	@Role({ RoleType.PJ })
	@BearerToken
	@GetMapping(path = "/usuario/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Busca eventos de um usuário específico.")
	public ResponseEntity<ListEventoResponse> buscarEventosByUsuario(
			@AuthenticationPrincipal JwtUserDTO jwtUserDTO,
			@RequestParam(required = true) Long pagina,
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) String descricao,
			@RequestParam(required = false) String data,
			@RequestParam(required = false) String cidade,
			@RequestParam(required = false) String estado,
			@RequestParam(required = false) Long faixaKm,
			@RequestParam(required = false) Long tipoEvento,
			@RequestParam(required = false) Long nivelHabilidade,
			@RequestParam(required = false) Boolean gratuito,
			@RequestParam(required = false) Boolean aprovado) {
		return ResponseEntity.ok(eventoService.listarEventos(pagina, nome, descricao, data, cidade, estado, faixaKm,
				tipoEvento, nivelHabilidade, gratuito, aprovado, usuarioService.loadUsuarioByJwt(jwtUserDTO).getId()));
	}
}