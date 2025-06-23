package br.edu.ifsp.spo.bike_integration.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NivelHabilidadeDTO {

	private String nome;
	private String descricao;
}
