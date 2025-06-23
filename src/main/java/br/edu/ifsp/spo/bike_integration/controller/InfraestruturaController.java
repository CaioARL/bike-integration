package br.edu.ifsp.spo.bike_integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.InfraestruturaCicloviaria;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.service.InfraestruturaCicloviariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/infraestrutura")
@Tag(name = "Infraestrutura", description = "Controller para operações relacionadas à infraestrutura cicloviária.")
public class InfraestruturaController {

	@Autowired
	private InfraestruturaCicloviariaService infraestruturaService;

	@Role(RoleType.PF)
	@BearerToken
	@GetMapping("/{id}")
	@Operation(summary = "Busca uma infraestrutura cicloviária por ID")
	public ResponseEntity<InfraestruturaCicloviaria> findById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(infraestruturaService.findById(id));
	}

	@Role(RoleType.PF)
	@BearerToken
	@GetMapping("/{id}/geojson")
	@Operation(summary = "Busca uma infraestrutura cicloviária por ID no formato GeoJson")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Infraestrutura cicloviária encontrada com sucesso"),
			@ApiResponse(responseCode = "404", description = "Infraestrutura cicloviária não encontrada")
	})
	public ResponseEntity<GeoJsonDTO> buscarInfraestruturaAsGeoJsonById(@PathVariable("id") Long id)
			throws NotFoundException {
		return ResponseEntity.ok(infraestruturaService.buscarInfraestruturaAsGeoJsonById(id));
	}

	@Role(RoleType.PF)
	@BearerToken
	@GetMapping("/geojson")
	@Operation(summary = "Lista infraestruturas cicloviárias no formato GeoJson com base em localização e raio")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Infraestruturas cicloviárias listadas com sucesso"),
			@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	})
	public ResponseEntity<GeoJsonDTO> buscarInfraestruturasAsGeoJson(
			@RequestParam Double latitude,
			@RequestParam Double longitude,
			@RequestParam Double raio) {
		return ResponseEntity.ok(infraestruturaService.buscarInfraestruturasAsGeoJson(latitude, longitude, raio));
	}
}