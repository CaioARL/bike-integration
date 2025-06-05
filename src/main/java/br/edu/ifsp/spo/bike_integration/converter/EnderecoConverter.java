package br.edu.ifsp.spo.bike_integration.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.model.dto.EnderecoDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EnderecoConverter implements AttributeConverter<EnderecoDTO, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(EnderecoDTO endereco) {
		try {
			return objectMapper.writeValueAsString(endereco);
		} catch (JsonProcessingException e) {
			throw new BikeIntegrationCustomException("Erro ao converter Endereco para JSON", e);
		}
	}

	@Override
	public EnderecoDTO convertToEntityAttribute(String json) {
		try {
			return objectMapper.readValue(json, EnderecoDTO.class);
		} catch (JsonProcessingException e) {
			throw new BikeIntegrationCustomException("Erro ao converter JSON para Endereco", e);
		}
	}
}
