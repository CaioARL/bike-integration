package br.edu.ifsp.spo.bike_integration.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAdmDTO {

	@Schema(example = "Maria")
	private String nome;

	@Schema(example = "mariasilva")
	private String nomeUsuario;

	@Schema(example = "mariasilva@gmail.com")
	private String email;

	@Schema(example = "78910")
	private String senha;
}
