package br.edu.ifsp.spo.bike_integration.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordenadasDTO {

	@Schema(example = "-49.0629788")
	private double latitude;

	@Schema(example = "-26.9244749")
	private double longitude;
}
