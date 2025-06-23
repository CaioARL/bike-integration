package br.edu.ifsp.spo.bike_integration.rest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.response.BrasilApiCepResponse;
import br.edu.ifsp.spo.bike_integration.rest.service.BrasilApiService;
import br.edu.ifsp.spo.bike_integration.rest.service.OpenStreetMapApiService;
import br.edu.ifsp.spo.bike_integration.util.FormatUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/endereco")
@Tag(name = "Endereço", description = "Controller para operações relacionadas a endereços.")
public class EnderecoController {

	@Autowired
	private BrasilApiService brasilApiService;

	@Autowired
	private OpenStreetMapApiService openStreetMapApiService;

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@GetMapping("/get/by/cep")
	@Operation(summary = "Busca endereço completo pelo cep.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso."),
			@ApiResponse(responseCode = "404", description = "Endereço não encontrado.") })
	public BrasilApiCepResponse buscarEnderecoPorCep(@RequestParam String cep) throws NotFoundException {
		return brasilApiService.buscarEnderecoPorCep(FormatUtils.formatCep(cep));
	}

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@GetMapping("/get/latAndLon/by/cepAndNumber")
	@Operation(summary = "Busca latitude e longitude pelo cep e número.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Latitude e longitude encontradas com sucesso."),
			@ApiResponse(responseCode = "404", description = "Latitude e longitude não encontradas.") })
	public Map<String, Double> buscarLatAndLonByCepAndNumber(@RequestParam String cep, @RequestParam Long numero)
			throws NotFoundException {
		return openStreetMapApiService.buscarCoordenadasPorEndereco(FormatUtils.formatEnderecoToOpenStreetMapApi(
				FormatUtils.convertToDto(brasilApiService.buscarEnderecoPorCep(cep), numero)));
	}

	@Role({ RoleType.PF, RoleType.PJ })
	@BearerToken
	@GetMapping("/get/cep/by/LatAndLon")
	@Operation(summary = "Busca cep pelo latitude e longitude.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Cep encontrado com sucesso."),
			@ApiResponse(responseCode = "404", description = "Cep não encontrado.") })
	public Map<String, String> buscarCepPorLatAndLon(@RequestParam String latitude, @RequestParam String longitude) {
		return openStreetMapApiService.buscarCepPorCoordenadas(latitude, longitude);
	}
}
