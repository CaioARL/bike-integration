package br.edu.ifsp.spo.bike_integration.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoDTO {

	@Schema(example = "Evento de lançamento do novo produto")
	private String nome;

	@Schema(example = "Evento de lançamento do novo produto")
	private String descricao;

	@Schema(example = "2021-10-10T10:00:00.000")
	private String data;

	@Hidden
	@Builder.Default
	private LocalDateTime dataAtualizacao = LocalDateTime.now();

	private EnderecoDTO endereco;

	@Schema(example = "10")
	private Long faixaKm;

	@Schema(example = "true")
	private Boolean gratuito;

	@Schema(example = "https://www.exemplo.com")
	private String urlSite;

	@Schema(example = "1")
	private Long idTipoEvento;

	@Schema(example = "1")
	private String idUsuario;

}
