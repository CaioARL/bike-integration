package br.edu.ifsp.spo.bike_integration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.dto.UsuarioLoginDTO;
import br.edu.ifsp.spo.bike_integration.exception.CryptoException;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import br.edu.ifsp.spo.bike_integration.repository.UsuarioRepository;
import br.edu.ifsp.spo.bike_integration.util.CryptoUtils;
import jakarta.mail.MessagingException;

@Service
public class LoginService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private SessaoService sessaoService;

	public Usuario login(UsuarioLoginDTO usuario) throws CryptoException, MessagingException {
		Usuario usuarioLogado;

		if (usuario.getNomeUsuario() != null) {
			usuarioLogado = this.usuarioRepository.findByNomeUsuario(usuario.getNomeUsuario()).orElse(null);
		} else if (usuario.getEmail() != null) {
			usuarioLogado = this.usuarioRepository.findByEmail(usuario.getEmail()).orElse(null);
		} else if (usuario.getCpf() != null) {
			usuarioLogado = this.usuarioRepository.findByCpf(usuario.getCpf()).orElse(null);
		} else if (usuario.getCnpj() != null) {
			usuarioLogado = this.usuarioRepository.findByCnpj(usuario.getCnpj()).orElse(null);
		} else {
			throw new IllegalArgumentException("Usuário não encontrado!");
		}

		if (!usuario.getSenha().isBlank() && usuarioLogado != null && !usuario.getSenha().equals(CryptoUtils
				.decrypt(usuarioLogado.getSenha(), CryptoUtils.getSecretKeyFromString(usuarioLogado.getHash())))) {
			throw new IllegalArgumentException("Usuário ou senha inválidos!");
		} else if (usuarioLogado == null) {
			throw new IllegalArgumentException("Usuário não encontrado!");
		}

		// Gerar token email
		// this.tokenService.generateToken(usuarioLogado.getEmail());

		// Enviar email com token
		// this.emailService.sendLoginTokenEmail(usuarioLogado);

		// Criar sessão
		this.sessaoService.create(usuarioLogado);

		return usuarioRepository.findById(usuarioLogado.getId()).orElse(null);
	}

	public void recoverPassword(String idUsuario, String novaSenha)
			throws CryptoException, MessagingException {
		Usuario usuario = this.usuarioRepository.findById(idUsuario).orElse(null);

		if (usuario == null) {
			throw new IllegalArgumentException("Usuário não encontrado!");
		}

		usuario.setHash(CryptoUtils.generateKeyAsString());
		usuario.setSenha(CryptoUtils.encrypt(novaSenha, usuario.getHash()));
		this.usuarioRepository.save(usuario);
	}

}
