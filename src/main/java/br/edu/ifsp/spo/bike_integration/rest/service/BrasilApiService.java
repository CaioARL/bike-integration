
package br.edu.ifsp.spo.bike_integration.rest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.hardcode.BrasilApiType;
import br.edu.ifsp.spo.bike_integration.hardcode.ConfiguracaoApiType;
import br.edu.ifsp.spo.bike_integration.model.ConfiguracaoApiExterna;
import br.edu.ifsp.spo.bike_integration.model.response.BrasilApiCepResponse;
import br.edu.ifsp.spo.bike_integration.model.response.BrasilApiCnpjResponse;
import br.edu.ifsp.spo.bike_integration.service.ConfiguracaoApiExternaService;
import br.edu.ifsp.spo.bike_integration.util.FormatUtils;
import jakarta.annotation.PostConstruct;

@Service
public class BrasilApiService {

	@Autowired
	private ConfiguracaoApiExternaService configuracaoApiService;

	@Autowired
	private ViaCepService viaCepService;

	private ConfiguracaoApiExterna configuracao;

	private RestTemplate restTemplate;

	@PostConstruct
	public void init() {
		configuracao = configuracaoApiService.getConfiguracaoByType(ConfiguracaoApiType.BRASIL_API);
		restTemplate = new RestTemplate(getClientHttpRequestFactory());
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(10000);
		factory.setReadTimeout(10000);
		return factory;
	}

	Logger logger = LoggerFactory.getLogger(BrasilApiService.class);

	public BrasilApiCepResponse buscarEnderecoPorCep(String cep) throws NotFoundException {
		if (!cep.matches("\\d{5}-?\\d{3}")) {
			throw new BikeIntegrationCustomException("CEP inválido: " + cep);
		}

		try {
			return restTemplate.getForObject(
					configuracao.getUrl() + BrasilApiType.CEP.getEndpoint() + FormatUtils.removeNonNumeric(cep),
					BrasilApiCepResponse.class);
		} catch (Exception e) {
			return buscarEnderecoViaCep(cep);
		}
	}

	private BrasilApiCepResponse buscarEnderecoViaCep(String cep) throws NotFoundException {
		try {
			return viaCepService.buscarEnderecoPorCep(cep);
		} catch (Exception e) {
			logger.error("Erro ao buscar endereço pelo CEP: " + cep, e);
			throw new BikeIntegrationCustomException("Erro ao buscar endereço pelo CEP: " + cep, e);
		}
	}

	public BrasilApiCnpjResponse buscarEmpresaPorCnpj(String cnpj) throws NotFoundException {
		if (!cnpj.matches("\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}")) {
			cnpj = FormatUtils.formatCnpj(cnpj);
		}

		try {
			return restTemplate.getForObject(
					configuracao.getUrl() + BrasilApiType.CNPJ.getEndpoint() + FormatUtils.removeNonNumeric(cnpj),
					BrasilApiCnpjResponse.class);
		} catch (Exception e) {
			logger.error("Erro ao buscar empresa pelo CNPJ: " + cnpj, e);
			throw new BikeIntegrationCustomException("Erro ao buscar empresa pelo CNPJ: " + cnpj, e);
		}
	}
}
