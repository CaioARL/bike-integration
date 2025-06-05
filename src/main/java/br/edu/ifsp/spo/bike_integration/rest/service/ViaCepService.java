package br.edu.ifsp.spo.bike_integration.rest.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.hardcode.ConfiguracaoApiType;
import br.edu.ifsp.spo.bike_integration.hardcode.ViaCepApiType;
import br.edu.ifsp.spo.bike_integration.model.ConfiguracaoApiExterna;
import br.edu.ifsp.spo.bike_integration.model.response.BrasilApiCepResponse;
import br.edu.ifsp.spo.bike_integration.model.response.ViaCepApiResponse;
import br.edu.ifsp.spo.bike_integration.model.response.BrasilApiCepResponse.Location;
import br.edu.ifsp.spo.bike_integration.model.response.BrasilApiCepResponse.Location.Coordinates;
import br.edu.ifsp.spo.bike_integration.service.ConfiguracaoApiExternaService;
import br.edu.ifsp.spo.bike_integration.util.FormatUtils;
import jakarta.annotation.PostConstruct;

@Service
public class ViaCepService {

	@Autowired
	private ConfiguracaoApiExternaService configuracaoApiService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OpenStreetMapApiService openStreetMapApiService;

	private ConfiguracaoApiExterna configuracao;

	@PostConstruct
	public void init() {
		configuracao = configuracaoApiService.getConfiguracaoByType(ConfiguracaoApiType.VIA_CEP);
	}

	Logger logger = LoggerFactory.getLogger(ViaCepService.class);

	public BrasilApiCepResponse buscarEnderecoPorCep(String cep) throws NotFoundException {
		if (!cep.matches("\\d{5}-?\\d{3}"))
			throw new IllegalArgumentException("CEP inválido.");

		try {
			return this.convertToBrasilApiResponse(restTemplate.getForObject(
					configuracao.getUrl() + ViaCepApiType.WS.getEndpoint() + FormatUtils.removeNonNumeric(cep)
							+ "/json",
					ViaCepApiResponse.class));
		} catch (Exception e) {
			logger.error("Erro ao buscar endereço pelo CEP: " + cep, e);
			throw new BikeIntegrationCustomException("Erro ao buscar endereço pelo CEP: " + cep, e);
		}
	}

	/*
	 * PRIVATE METHODS
	 */

	private BrasilApiCepResponse convertToBrasilApiResponse(ViaCepApiResponse viaCepApiResponse) {
		BrasilApiCepResponse brasilApiCepResponse = new BrasilApiCepResponse();
		brasilApiCepResponse.setCep(viaCepApiResponse.getCep());
		brasilApiCepResponse.setState(viaCepApiResponse.getUf());
		brasilApiCepResponse.setCity(viaCepApiResponse.getLocalidade());
		brasilApiCepResponse.setNeighborhood(viaCepApiResponse.getBairro());
		brasilApiCepResponse.setStreet(viaCepApiResponse.getLogradouro());

		Map<String, String> locMap = openStreetMapApiService.buscaCoordenadasPorCep(viaCepApiResponse.getCep());
		Coordinates coordinates = locMap != null
				? new Coordinates(Double.valueOf(locMap.get("lon")), Double.valueOf(locMap.get("lat")))
				: null;

		Location location = Location.builder().type("Point").coordinates(coordinates).build();
		brasilApiCepResponse.setLocation(location);
		return brasilApiCepResponse;
	}

}
