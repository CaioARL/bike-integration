package br.edu.ifsp.spo.bike_integration.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifsp.spo.bike_integration.aws.service.S3Service;
import br.edu.ifsp.spo.bike_integration.dto.JwtUserDTO;
import br.edu.ifsp.spo.bike_integration.dto.UsuarioAdmDTO;
import br.edu.ifsp.spo.bike_integration.dto.UsuarioDTO;
import br.edu.ifsp.spo.bike_integration.dto.UsuarioUpdateDTO;
import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import br.edu.ifsp.spo.bike_integration.repository.UsuarioRepository;
import br.edu.ifsp.spo.bike_integration.rest.service.OpenStreetMapApiService;
import br.edu.ifsp.spo.bike_integration.util.FormatUtils;
import br.edu.ifsp.spo.bike_integration.util.S3Utils;
import br.edu.ifsp.spo.bike_integration.util.validate.CpfValidate;
import br.edu.ifsp.spo.bike_integration.util.validate.CpfValidate.CpfValidationResult;
import jakarta.transaction.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private OpenStreetMapApiService openStreetMapApiService;

	@Autowired
	private NivelHabilidadeService nivelHabilidadeService;

	@Autowired
	private SessaoService sessaoService;

	@Autowired
	private S3Service s3Service;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	public Usuario loadUsuarioById(String id) {
		return usuarioRepository.findById(id).orElse(null);
	}

	public Usuario loadUsuarioByEmail(String email) {
		return usuarioRepository.findByEmail(email).orElse(null);
	}

	public Usuario loadUsuarioByNomeUsuario(String nomeUsuario) {
		return usuarioRepository.findByNomeUsuario(nomeUsuario).orElse(null);
	}

	public Usuario loadUsuarioByJwt(JwtUserDTO jwtUserDTO) {
		return usuarioRepository.findByEmail(jwtUserDTO.getEmail())
				.orElse(usuarioRepository.findByNomeUsuario(jwtUserDTO.getNickname()).orElse(null));
	}

	public Usuario createUsuario(UsuarioDTO usuarioDto) {
		// Busca as coordenadas do endereço
		Map<String, Double> coordenadas = openStreetMapApiService
				.buscarCoordenadasPorEndereco(FormatUtils.formatEnderecoToOpenStreetMapApi(usuarioDto.getEndereco()));
		usuarioDto.getEndereco().setLatitude(coordenadas.get("lat"));
		usuarioDto.getEndereco().setLongitude(coordenadas.get("lon"));

		// Salva o usuário
		Usuario usuario = usuarioRepository.save(Usuario.builder().nome(usuarioDto.getNome())
				.nomeUsuario(usuarioDto.getNomeUsuario()).endereco(usuarioDto.getEndereco())
				.email(usuarioDto.getEmail()).senha(usuarioDto.getSenha()).cpf(usuarioDto.getCpf())
				.cnpj(usuarioDto.getCnpj())
				.nivelHabilidade(nivelHabilidadeService.loadNivelHabilidade(usuarioDto.getNivelHabilidade()))
				.role(usuarioDto.getCpf() != null ? RoleType.PF
						: usuarioDto.getCnpj() != null ? RoleType.PJ : RoleType.ADMIN)
				.build());

		// Salva sessão
		sessaoService.create(usuario);

		return this.loadUsuarioById(usuario.getId());

	}

	public Usuario refreshSession(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
		if (usuario != null) {
			sessaoService.create(usuario);
		}
		return usuarioRepository.findById(usuario.getId()).orElse(null);
	}

	public Usuario createUsuarioAdm(UsuarioAdmDTO usuarioAdmoDto) {
		// Salva o usuário
		Usuario usuario = usuarioRepository.saveAndFlush(Usuario.builder().nome(usuarioAdmoDto.getNome())
				.nomeUsuario(usuarioAdmoDto.getNomeUsuario()).email(usuarioAdmoDto.getEmail())
				.senha(usuarioAdmoDto.getSenha())
				.role(RoleType.ADMIN).build());

		// Salva sessão
		sessaoService.create(usuario);

		return this.loadUsuarioById(usuario.getId());
	}

	@Modifying
	@Transactional
	public Usuario updateUsuario(String id, UsuarioUpdateDTO usuarioDto) {
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if (usuario == null) {
			return null;
		}
		usuario.setNome(usuarioDto.getNome());
		usuario.setNomeUsuario(usuarioDto.getNomeUsuario());
		usuario.setEndereco(usuarioDto.getEndereco());
		usuario.setEmail(usuarioDto.getEmail());
		usuario.setNivelHabilidade(nivelHabilidadeService.loadNivelHabilidade(usuarioDto.getNivelHabilidade()));
		return usuarioRepository.save(usuario);
	}

	public void updateFotoUsuario(String id, MultipartFile file) {
		try {
			Usuario usuario = usuarioRepository.findById(id).orElse(null);
			if (usuario != null) {
				String s3Key = S3Utils.createS3Key("usuario", id.toString(), file);
				PutObjectResponse response = s3Service.put(S3Utils.createRestPutObjectRequest(bucketName, s3Key),
						file.getBytes());
				if (response.sdkHttpResponse().isSuccessful()) {
					usuario.setS3Url(s3Service.getUrl(s3Key));
					usuarioRepository.save(usuario);
				} else {
					throw new BikeIntegrationCustomException("Erro ao salvar a foto do usuário.");
				}
			}
		} catch (IOException e) {
			throw new BikeIntegrationCustomException("Erro ao salvar foto do usuário", e);
		}
	}

	public void deleteUsuario(String id) {
		usuarioRepository.deleteById(id);
	}

	public CpfValidationResult validateCpf(String cpf) {
		return CpfValidate.validate(cpf).isValid()
				? (usuarioRepository.existsByCpf(cpf) ? (new CpfValidationResult(false, "CPF já cadastrado."))
						: (new CpfValidationResult(true, "CPF disponível.")))
				: new CpfValidationResult(false, "CPF inválido.");
	}

	public Boolean validateUsuarioByCnpj(String cnpj) {
		return usuarioRepository.existsByCnpj(cnpj);
	}

	public Boolean validateUsuarioByEmailOrNomeUsuario(String nomeUsuario, String email) {
		return usuarioRepository.existsByNomeUsuarioOrEmail(nomeUsuario, email);
	}
}
