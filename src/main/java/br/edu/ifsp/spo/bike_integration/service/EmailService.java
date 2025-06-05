package br.edu.ifsp.spo.bike_integration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UsuarioService usuarioService;

	// Constants for email subjects
	private static final String SUBJECT_CADASTRO = "Token de Cadastro - Bicity App";
	private static final String SUBJECT_RECUPERACAO = "Token de Recuperação - Bicity App";
	private static final String SUBJECT_LOGIN = "Token de Login - Bicity App";
	private static final String SUBJECT_CUSTOM = "Mensagem de Bicity App";

	// Constants for HTML template parts
	private static final String HTML_HEADER = "<!DOCTYPE html><html lang='pt-br'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'></head><body style='background-color: #f8f9fa; padding: 20px;'><div class='container' style='max-width: 600px; background: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 20px;'>";
	private static final String HTML_FOOTER = "<p style='margin-top: 30px; font-size: 12px; color: #6c757d;'>Atenciosamente,<br>Equipe Bicity App</p></div></body></html>";

	// Envia um e-mail com o token de cadastro para o usuário.
	public void sendCadastroTokenEmail(String email, String token) throws MessagingException {
		String htmlContent = buildEmailContent("", "Obrigado por se registrar no <strong>Bicity App</strong>.",
				"Seu token de cadastro é:", token);
		sendEmail(email, SUBJECT_CADASTRO, htmlContent);
	}

	// Envia um e-mail com o token de recuperação para o usuário.
	public void sendRecuperacaoTokenEmail(String email, String token) throws MessagingException {
		if (usuarioService.loadUsuarioByEmail(email) == null) {
			throw new BikeIntegrationCustomException("Usuário não encontrado com o e-mail informado.");
		}

		String htmlContent = buildEmailContent("",
				"Você solicitou a recuperação de sua senha no <strong>Bicity App</strong>.",
				"Seu token de recuperação é:", token);
		sendEmail(email, SUBJECT_RECUPERACAO, htmlContent);
	}

	// Envia um e-mail com o token de login para o usuário.
	public void sendLoginTokenEmail(Usuario usuario) throws MessagingException {
		String htmlContent = buildEmailContent(usuario.getNome(),
				"Você solicitou um token de login no <strong>Bicity App</strong>.", "Seu token de login é:",
				tokenService.getLastTokenByEmail(usuario.getEmail()).getTokenGerado());
		sendEmail(usuario.getEmail(), SUBJECT_LOGIN, htmlContent);
	}

	// Envia uma mensagem personalizada para o destinatário.
	public void sendAnyMessageEmail(String message, String to) throws MessagingException {
		String htmlContent = buildEmailContent("", message, "", "");
		sendEmail(to, SUBJECT_CUSTOM, htmlContent);
	}

	/**
	 * PRIVATE METHODS
	 */

	// Constrói o conteúdo do e-mail com base no template.
	private String buildEmailContent(String nome, String message, String tokenMessage, String token) {
		return HTML_HEADER + "<h1 style='font-size: 24px; color: #007bff;'>Olá " + nome + ",</h1>" + "<p>" + message
				+ "</p>" + (tokenMessage.isEmpty() ? "" : "<p>" + tokenMessage + "</p>")
				+ (token.isEmpty() ? ""
						: "<div style='text-align: center; margin: 20px 0;'><span style='font-size: 20px; font-weight: bold; color: #28a745;'>"
								+ token + "</span></div>")
				+ HTML_FOOTER;
	}

	// Método genérico para enviar um e-mail.
	private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);
		mailSender.send(mimeMessage);
	}
}
