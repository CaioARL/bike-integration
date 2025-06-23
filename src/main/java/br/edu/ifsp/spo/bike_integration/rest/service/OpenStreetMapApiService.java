package br.edu.ifsp.spo.bike_integration.rest.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.hardcode.ConfiguracaoApiType;
import br.edu.ifsp.spo.bike_integration.hardcode.OpenStreetMapApiType;
import br.edu.ifsp.spo.bike_integration.model.ConfiguracaoApiExterna;
import br.edu.ifsp.spo.bike_integration.model.dto.CoordenadasDTO;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.model.response.RouteResponseDTO;
import br.edu.ifsp.spo.bike_integration.service.ConfiguracaoApiExternaService;
import br.edu.ifsp.spo.bike_integration.util.geojson.GeoJsonInfraestruturaUtils;
import jakarta.annotation.PostConstruct;

@Service
public class OpenStreetMapApiService {
	public static final Logger logger = LoggerFactory.getLogger(OpenStreetMapApiService.class);

	@Autowired
	private ConfiguracaoApiExternaService configuracaoApiService;

	@Autowired
	private RestTemplate restTemplate;

	private ConfiguracaoApiExterna configuracao;
	private ConfiguracaoApiExterna routeConfiguracao;

	@PostConstruct
	public void init() {
		configuracao = configuracaoApiService.getConfiguracaoByType(ConfiguracaoApiType.OPEN_STREET_MAP_API);
		routeConfiguracao = configuracaoApiService.getConfiguracaoByType(ConfiguracaoApiType.ROUTING_OPEN_STREET_MAP);
	}

	private void validarString(String valor, String mensagem) {
		if (valor == null || valor.isEmpty())
			throw new IllegalArgumentException(mensagem);
	}

	private void validarCoordenadas(List<CoordenadasDTO> coordenadas) {
		if (coordenadas == null || coordenadas.size() < 2)
			throw new IllegalArgumentException("É necessário pelo menos duas coordenadas");
	}

	public Map<String, Double> buscarCoordenadasPorEndereco(String endereco) {
		validarString(endereco, "Endereço inválido.");
		try {
			ResponseEntity<Map<String, Object>[]> responseEntity = restTemplate.exchange(
					configuracao.getUrl() + OpenStreetMapApiType.SEARCH.getEndpoint() + "?q=" + endereco
							+ "&format=jsonv2",
					HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>[]>() {
					});
			Map<String, Object>[] response = responseEntity.getBody();
			return extrairLatLon(response);
		} catch (Exception e) {
			logger.error("Erro ao buscar coordenadas do endereço: {}", endereco, e);
			throw new BikeIntegrationCustomException("Erro ao buscar coordenadas do endereço: " + endereco, e);
		}
	}

	public Map<String, String> buscarCepPorCoordenadas(String latitude, String longitude) {
		validarString(latitude, "Latitude inválida.");
		validarString(longitude, "Longitude inválida.");
		try {
			ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
					configuracao.getUrl() + OpenStreetMapApiType.REVERSE.getEndpoint() + "?lat=" + latitude + "&lon="
							+ longitude + "&format=jsonv2",
					HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
					});
			Map<String, Object> response = responseEntity.getBody();
			if (response != null) {
				@SuppressWarnings("unchecked")
				Map<String, Object> address = (Map<String, Object>) response.get("address");
				if (address != null && address.get("postcode") != null) {
					String cep = (String) address.get("postcode");
					return Map.of("cep", cep.replace("-", ""));
				}
			}
			throw new IllegalArgumentException("CEP não encontrado para as coordenadas.");
		} catch (Exception e) {
			logger.error("Erro ao buscar cep das coordenadas: {}, {}", latitude, longitude, e);
			throw new BikeIntegrationCustomException(
					"Erro ao buscar cep das coordenadas: " + latitude + ", " + longitude, e);
		}
	}

	public Map<String, String> buscaCoordenadasPorCep(String cep) {
		validarString(cep, "CEP inválido.");
		try {
			ResponseEntity<Map<String, Object>[]> responseEntity = restTemplate.exchange(
					configuracao.getUrl() + OpenStreetMapApiType.SEARCH.getEndpoint() + "?q=" + cep + "&format=jsonv2",
					HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>[]>() {
					});
			Map<String, Object>[] response = responseEntity.getBody();
			Map<String, Double> latLon = extrairLatLon(response);
			return Map.of("lat", latLon.get("lat").toString(), "lon", latLon.get("lon").toString());
		} catch (Exception e) {
			logger.error("Erro ao buscar coordenadas do CEP: {}", cep, e);
			throw new BikeIntegrationCustomException("Erro ao buscar coordenadas do CEP: " + cep, e);
		}
	}

	public GeoJsonDTO buscarRotaPorString(String coords) {
		if (coords == null || coords.isEmpty()) {
			throw new IllegalArgumentException("Coordenadas não informadas");
		}
		List<CoordenadasDTO> coordenadas = java.util.Arrays.stream(coords.split(";"))
				.map(pair -> pair.split(","))
				.filter(arr -> arr.length == 2)
				.map(arr -> {
					try {
						double lat = Double.parseDouble(arr[0]);
						double lon = Double.parseDouble(arr[1]);
						return new CoordenadasDTO(lat, lon);
					} catch (NumberFormatException e) {
						return null;
					}
				})
				.filter(java.util.Objects::nonNull)
				.toList();
		if (coordenadas.isEmpty()) {
			throw new IllegalArgumentException("Coordenadas inválidas");
		}
		return buscarRota(coordenadas);
	}

	public GeoJsonDTO buscarRota(List<CoordenadasDTO> coordenadas) {
		validarCoordenadas(coordenadas);
		String coords = coordenadas.stream()
				.map(c -> c.getLatitude() + "," + c.getLongitude())
				.reduce((a, b) -> a + ";" + b)
				.orElse("");
		String url = routeConfiguracao.getUrl() + coords + "?overview=false&geometries=polyline&steps=true";
		try {
			ResponseEntity<RouteResponseDTO> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					HttpEntity.EMPTY,
					RouteResponseDTO.class);
			RouteResponseDTO routeResponse = response.getBody();
			if (routeResponse == null)
				throw new BikeIntegrationCustomException("Resposta vazia da API de rota");
			return GeoJsonInfraestruturaUtils.convertPolylineToGeoJson(routeResponse);
		} catch (Exception e) {
			logger.error("Erro ao buscar rota: {}", url, e);
			throw new BikeIntegrationCustomException("Erro ao buscar rota", e);
		}
	}

	/*
	 * PRIVATE METHODS
	 */
	private Map<String, Double> extrairLatLon(Map<String, Object>[] response) {
		if (response != null && response.length > 0) {
			Map<String, Object> firstResult = response[0];
			Double lat = Double.valueOf((String) firstResult.get("lat"));
			Double lon = Double.valueOf((String) firstResult.get("lon"));
			return Map.of("lat", lat, "lon", lon);
		}
		throw new IllegalArgumentException("Coordenadas não encontradas.");
	}

}
