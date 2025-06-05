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
public class UsuarioLoginDTO {

	@Schema(example = "JoãoSilva1")
	private String nomeUsuario;

	@Schema(example = "joaosilva1@gmail.com")
	private String email;

	@Schema(example = "123456")
	private String cpf;

	@Schema(example = "123456")
	private String cnpj;

	@Schema(example = "123456")
	private String senha;
}
