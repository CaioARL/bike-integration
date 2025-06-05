package br.edu.ifsp.spo.bike_integration.dto;

import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class AvaliacaoDTO {

	@Schema(example = "1")
	private String idUsuario;

	@Schema(example = "1")
	private Long idInfraestruturaCicloviaria;

	@Schema(example = "5")
	private Integer nota;

	@Schema(example = "Muito bom")
	private String comentario;
}
