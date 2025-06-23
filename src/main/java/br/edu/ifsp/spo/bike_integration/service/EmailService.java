package br.edu.ifsp.spo.bike_integration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
	@Async
	public void sendCadastroTokenEmail(String email, String token) throws MessagingException {
		String htmlContent = buildHtmlMessage(
				"https://cdn-icons-png.flaticon.com/512/190/190411.png",
				"Bem-vindo ao Bicity!",
				"#007bff",
				"Obrigado por se registrar no <strong>Bicity App</strong>.",
				"<div style='margin:24px 0;'><span style='font-size: 20px; font-weight: bold; color: #28a745;'>Seu token de cadastro:</span><br>"
						+ "<span style='font-size: 32px; font-weight: bold; color: #007bff; letter-spacing:2px;'>"
						+ token + "</span></div>"
						+ "<p style='font-size:15px;color:#555;'>Use este token para ativar sua conta e aproveitar todos os recursos da plataforma.</p>");
		sendEmail(email, SUBJECT_CADASTRO, htmlContent);
	}

	// Envia um e-mail com o token de recuperação para o usuário.
	@Async
	public void sendRecuperacaoTokenEmail(String email, String token) throws MessagingException {
		if (usuarioService.loadUsuarioByEmail(email) == null) {
			throw new BikeIntegrationCustomException("Usuário não encontrado com o e-mail informado.");
		}
		String htmlContent = buildHtmlMessage(
				"https://cdn-icons-png.flaticon.com/512/159/159604.png",
				"Recuperação de Senha",
				"#fd7e14",
				"Você solicitou a recuperação de sua senha no <strong>Bicity App</strong>.",
				"<div style='margin:24px 0;'><span style='font-size: 20px; font-weight: bold; color: #fd7e14;'>Seu token de recuperação:</span><br>"
						+ "<span style='font-size: 32px; font-weight: bold; color: #fd7e14; letter-spacing:2px;'>"
						+ token + "</span></div>"
						+ "<p style='font-size:15px;color:#555;'>Se não foi você, ignore este e-mail.</p>");
		sendEmail(email, SUBJECT_RECUPERACAO, htmlContent);
	}

	// Envia um e-mail com o token de login para o usuário.
	@Async
	public void sendLoginTokenEmail(Usuario usuario) throws MessagingException {
		String token = tokenService.getLastTokenByEmail(usuario.getEmail()).getTokenGerado();
		String htmlContent = buildHtmlMessage(
				"https://cdn-icons-png.flaticon.com/512/3064/3064197.png",
				"Olá, " + usuario.getNome() + "!",
				"#007bff",
				"Você solicitou um token de login no <strong>Bicity App</strong>.",
				"<div style='margin:24px 0;'><span style='font-size: 20px; font-weight: bold; color: #007bff;'>Seu token de login:</span><br>"
						+ "<span style='font-size: 32px; font-weight: bold; color: #007bff; letter-spacing:2px;'>"
						+ token + "</span></div>"
						+ "<p style='font-size:15px;color:#555;'>Use este token para acessar sua conta com segurança.</p>");
		sendEmail(usuario.getEmail(), SUBJECT_LOGIN, htmlContent);
	}

	// Envia uma mensagem personalizada para o destinatário.
	@Async
	public void sendAnyMessageEmail(String message, String to) throws MessagingException {
		String htmlContent = buildHtmlMessage(
				"https://cdn-icons-png.flaticon.com/512/565/565547.png",
				"Mensagem do Bicity App",
				"#007bff",
				message,
				"");
		sendEmail(to, SUBJECT_CUSTOM, htmlContent);
	}

	// Envia e-mail de aprovação/reprovação de evento para o usuário.
	@Async
	public void sendEventoStatusEmail(String to, String nomeUsuario, String nomeEvento, boolean aprovado,
			String observacoes) throws MessagingException {
		String subject;
		String htmlContent;
		if (aprovado) {
			subject = "Evento aprovado - Bicity App";
			htmlContent = buildHtmlMessage(
					"https://cdn-icons-png.flaticon.com/512/190/190411.png",
					"Parabéns, " + nomeUsuario + "!",
					"#28a745",
					String.format(
							"Seu evento <strong>'%s'</strong> foi <span style='color: #28a745; font-weight:bold;'>aprovado</span> e já está disponível na plataforma.",
							nomeEvento),
					"<p style='font-size:15px;color:#555;'>Agradecemos por contribuir com a comunidade Bicity!</p>");
		} else {
			subject = "Evento não aprovado - Bicity App";
			String obsHtml = (observacoes != null && !observacoes.isEmpty())
					? String.format(
							"<div style='margin:16px 0; padding:12px; background:#fff3cd; border-radius:6px; color:#856404; border:1px solid #ffeeba;'><strong>Motivo:</strong> %s</div>",
							observacoes)
					: "";
			htmlContent = buildHtmlMessage(
					"https://cdn-icons-png.flaticon.com/512/463/463612.png",
					"Olá, " + nomeUsuario,
					"#dc3545",
					String.format("Infelizmente seu evento <strong>'%s'</strong> não foi aprovado.", nomeEvento),
					obsHtml + "<p style='font-size:15px;color:#555;'>Consulte o motivo acima e, se desejar, ajuste seu evento para uma nova análise.<br>Estamos à disposição para dúvidas!</p>");
		}
		sendEmail(to, subject, htmlContent);
	}

	/**
	 * PRIVATE METHODS
	 */

	// Método genérico para enviar um e-mail.
	private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);
		mailSender.send(mimeMessage);
	}

	private String buildHtmlMessage(String iconUrl, String title, String titleColor, String mainMessage,
			String extraHtml) {
		return HTML_HEADER +
				"<div style='text-align:center;'>"
				+ (iconUrl != null
						? "<img src='" + iconUrl + "' width='64' style='margin-bottom:16px;' alt='Icon'/><br>"
						: "")
				+ (title != null ? "<h2 style='color:" + titleColor + ";'>" + title + "</h2>" : "")
				+ (mainMessage != null ? "<p style='font-size:18px;'>" + mainMessage + "</p>" : "")
				+ (extraHtml != null ? extraHtml : "")
				+ "</div>"
				+ HTML_FOOTER;
	}
}
