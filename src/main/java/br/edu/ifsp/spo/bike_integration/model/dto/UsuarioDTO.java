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
public class UsuarioDTO {

	@Schema(example = "João da Silva")
	private String nome;

	@Schema(example = "joaosilva")
	private String nomeUsuario;

	private EnderecoDTO endereco;

	@Schema(example = "joaosilva@gmail.com")
	private String email;

	@Schema(example = "123456")
	private String senha;

	@Schema(example = "123456")
	private String cpf;

	@Schema(example = "123456")
	private String cnpj;

	@Schema(example = "1")
	private Long nivelHabilidade;
}
