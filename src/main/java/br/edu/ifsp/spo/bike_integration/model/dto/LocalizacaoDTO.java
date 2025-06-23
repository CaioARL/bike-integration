package br.edu.ifsp.spo.bike_integration.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalizacaoDTO {

	private CoordenadasDTO coordenadas;
}
