package br.edu.ifsp.spo.bike_integration.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.edu.ifsp.spo.bike_integration.converter.EnderecoConverter;
import br.edu.ifsp.spo.bike_integration.dto.EnderecoDTO;
import br.edu.ifsp.spo.bike_integration.exception.CryptoException;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.util.CryptoUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario {

	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "nome_usuario", nullable = false, unique = true)
	private String nomeUsuario;

	@Column(name = "endereco", nullable = false)
	@Convert(converter = EnderecoConverter.class)
	private EnderecoDTO endereco;

	@Column(name = "e-mail", nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private RoleType role;

	@JsonIgnore
	@Column(name = "senha", nullable = false)
	private String senha;

	@JsonIgnore
	@Column(name = "hash", nullable = false)
	private String hash;

	@Column(name = "cpf", nullable = true)
	private String cpf;

	@Column(name = "cnpj", nullable = true)
	private String cnpj;

	@Column(name = "dt_criacao", nullable = false)
	private LocalDateTime dtCriacao;

	@ManyToOne
	@JoinColumn(name = "id_nivel_habilidade", nullable = true)
	private NivelHabilidade nivelHabilidade;

	@OneToOne(mappedBy = "usuario", fetch = FetchType.EAGER)
	private Sessao sessao;

	@Column(name = "s3_url")
	private String s3Url;

	@PrePersist
	public void prePersist() throws CryptoException {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
		this.dtCriacao = LocalDateTime.now();
		this.hash = CryptoUtils.generateKeyAsString();
		this.senha = CryptoUtils.encrypt(this.senha, this.hash);
	}
}
