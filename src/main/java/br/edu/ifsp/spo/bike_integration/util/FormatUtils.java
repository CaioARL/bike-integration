
package br.edu.ifsp.spo.bike_integration.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifsp.spo.bike_integration.model.dto.EnderecoDTO;
import br.edu.ifsp.spo.bike_integration.model.response.BrasilApiCepResponse;

public class FormatUtils {

	private FormatUtils() {
	}

	public static String formatCpf(String cpf) {
		if (cpf == null || cpf.isEmpty()) {
			return cpf;
		}
		cpf = cpf.replaceAll("\\D", ""); // Remove tudo que não for número
		return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}

	public static String formatCnpj(String cnpj) {
		if (cnpj == null || cnpj.isEmpty()) {
			return cnpj;
		}
		cnpj = cnpj.replaceAll("\\D", ""); // Remove tudo que não for número
		return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
	}

	public static String formatCep(String cep) {
		if (cep == null || cep.isEmpty()) {
			return cep;
		}
		cep = cep.replaceAll("\\D", ""); // Remove tudo que não for número
		return cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
	}

	public static String removeNonNumeric(String str) {
		return str.replaceAll("\\D", "");
	}

	public static String formatEnderecoToOpenStreetMapApi(EnderecoDTO endereco) {
		return endereco.getRua() + ", " + endereco.getNumero() + " - " + endereco.getCidade() + " - "
				+ endereco.getEstado();
	}

	public static EnderecoDTO convertToDto(BrasilApiCepResponse endereco, Long numero) {
		return EnderecoDTO.builder().cep(endereco.getCep()).estado(endereco.getState()).cidade(endereco.getCity())
				.bairro(endereco.getNeighborhood()).rua(endereco.getStreet()).numero(numero).build();
	}

	public static EnderecoDTO convertToDto(String endereco) throws JsonProcessingException {
		JsonNode json = new ObjectMapper().readTree(endereco);

		return EnderecoDTO.builder().cep(json.get("cep").asText()).estado(json.get("estado").asText())
				.cidade(json.get("cidade").asText()).bairro(json.get("bairro").asText())
				.rua(json.get("logradouro").asText()).numero(json.get("numero").asLong()).build();
	}
}