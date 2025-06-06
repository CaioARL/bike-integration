package br.edu.ifsp.spo.bike_integration.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.factory.GeoJsonUtilFactory;
import br.edu.ifsp.spo.bike_integration.hardcode.PaginationType;
import br.edu.ifsp.spo.bike_integration.model.Evento;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import br.edu.ifsp.spo.bike_integration.model.dto.EventoDTO;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.model.response.ListEventoResponse;
import br.edu.ifsp.spo.bike_integration.repository.EventoRepository;
import br.edu.ifsp.spo.bike_integration.rest.service.OpenStreetMapApiService;
import br.edu.ifsp.spo.bike_integration.service.aws.S3Service;
import br.edu.ifsp.spo.bike_integration.util.DateUtils;
import br.edu.ifsp.spo.bike_integration.util.FormatUtils;
import br.edu.ifsp.spo.bike_integration.util.ObjectMapperUtils;
import br.edu.ifsp.spo.bike_integration.util.S3Utils;
import br.edu.ifsp.spo.bike_integration.websocket.CustomWebSocketHandler;
import br.edu.ifsp.spo.bike_integration.websocket.EventoSocketMessageDTO;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class EventoService {

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private TipoEventoService tipoEventoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private OpenStreetMapApiService openStreetMapApiService;

	@Autowired
	private S3Service s3Service;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	public Evento buscarEvento(Long id) {
		return eventoRepository.findById(id).orElse(null);
	}

	public GeoJsonDTO buscarEventoAsGeoJsonById(Long id) {
		return GeoJsonUtilFactory.convertEventosToGeoJson(this.buscarEvento(id));
	}

	public List<Evento> buscarEventosByRadius(Double latitude, Double longitude, Double raio) {
		return this.getEventosProximosByLocation(latitude, longitude, raio);
	}

	public ListEventoResponse listarEventos(Long pagina, String nome, String descricao, String data, String cidade,
			String estado, Long faixaKm, Long tipoEvento, Long nivelHabilidade, Boolean gratuito, Boolean aprovado,
			String idUsuario) {

		Long limit = PaginationType.RESULTS_PER_PAGE.getValue();

		Long offset = (pagina - 1) * limit;

		String dataAjustada = DateUtils.fixFormattDate(data);

		List<Evento> eventos = eventoRepository.findAll(limit, offset, nome, descricao, dataAjustada, cidade, estado,
				faixaKm, tipoEvento, nivelHabilidade, gratuito, aprovado, idUsuario);

		Long count = eventoRepository.countAll(nome, descricao, dataAjustada, cidade, estado, faixaKm, tipoEvento,
				nivelHabilidade, gratuito, aprovado, idUsuario);

		Long totalPaginas = (long) Math.ceil(count / (double) limit);

		return ListEventoResponse.builder().eventos(eventos).totalRegistros(count).totalPaginas(totalPaginas).build();
	}

	public Long countAllEventos() {
		return eventoRepository.countAll(null, null, null, null, null, null, null, null, null, null, null);
	}

	public Evento createEvento(EventoDTO eventoDto, String username) {
		Evento evento = this.createEventoInternal(eventoDto);
		this.sendWebSocketMessage(usuarioService.loadUsuarioByNomeUsuario(username), "create",
				"Um novo evento foi criado por outro usuário, atualize a lista.");
		return evento;
	}

	public void updateEvento(Long id, EventoDTO eventoDto) {
		Evento evento = eventoRepository.findById(id).orElse(null);
		if (evento != null) {
			Map<String, Double> coordenadas = openStreetMapApiService
					.buscarCoordenadasPorEndereco(
							FormatUtils.formatEnderecoToOpenStreetMapApi(eventoDto.getEndereco()));
			eventoDto.getEndereco().setLatitude(coordenadas.get("lat"));
			eventoDto.getEndereco().setLongitude(coordenadas.get("lon"));

			Usuario usuario = usuarioService.loadUsuarioById(eventoDto.getIdUsuario());

			evento.setNome(eventoDto.getNome());
			evento.setDescricao(eventoDto.getDescricao());
			evento.setData(DateUtils.parseDate(eventoDto.getData()));
			evento.setDtAtualizacao(eventoDto.getDataAtualizacao());
			evento.setEndereco(eventoDto.getEndereco());
			evento.setTipoEvento(tipoEventoService.loadTipoEvento(eventoDto.getIdTipoEvento()));
			evento.setFaixaKm(eventoDto.getFaixaKm());
			evento.setGratuito(eventoDto.getGratuito());
			evento.setValor(eventoDto.getValor());
			evento.setUrlSite(eventoDto.getUrlSite());
			evento.setUsuario(usuario);
			evento.setAprovado(false);

			eventoRepository.save(evento);
		}
	}

	public void aprovarEvento(Long id, Boolean aprovar) {
		Evento evento = eventoRepository.findById(id).orElse(null);
		if (evento != null) {
			evento.setAprovado(aprovar);
			eventoRepository.save(evento);
		} else {
			throw new BikeIntegrationCustomException("Evento não encontrado.");
		}
	}

	public void deleteEvento(Long id, String username) {
		Usuario usuario = usuarioService.loadUsuarioByNomeUsuario(username);
		Evento evento = eventoRepository.findById(id).orElse(null);
		if (evento != null) {
			eventoRepository.delete(evento);
			this.sendWebSocketMessage(usuario, "delete", "Um evento foi excluído por outro usuário, atualize a lista.");
		}
	}

	public void deleteEventosByUsuario(String idUsuario) {
		Usuario usuario = usuarioService.loadUsuarioById(idUsuario);
		eventoRepository.deleteByUsuario(usuario);
	}

	public void updateFotoEvento(Long id, MultipartFile file) {
		try {
			Evento evento = eventoRepository.findById(id).orElse(null);
			if (evento != null) {
				String s3Key = S3Utils.createS3Key("evento", evento.getId().toString(), file);
				PutObjectResponse response = s3Service.put(S3Utils.createRestPutObjectRequest(bucketName, s3Key),
						file.getBytes());
				if (response.sdkHttpResponse().isSuccessful()) {
					evento.setS3Url(s3Service.getUrl(s3Key));
					eventoRepository.save(evento);
				} else {
					throw new BikeIntegrationCustomException("Erro ao salvar a foto do evento.");
				}
			}
		} catch (Exception | Error e) {
			throw new BikeIntegrationCustomException(
					"Erro ao atualizar a foto do evento: " + e.getCause().getMessage());
		}
	}

	/*
	 * PRIVATE METHODS
	 */

	private Evento createEventoInternal(EventoDTO eventoDto) {
		Map<String, Double> coordenadas = openStreetMapApiService
				.buscarCoordenadasPorEndereco(FormatUtils.formatEnderecoToOpenStreetMapApi(eventoDto.getEndereco()));
		eventoDto.getEndereco().setLatitude(coordenadas.get("lat"));
		eventoDto.getEndereco().setLongitude(coordenadas.get("lon"));

		Usuario usuario = usuarioService.loadUsuarioById(eventoDto.getIdUsuario());

		return eventoRepository.save(Evento.builder().nome(eventoDto.getNome()).descricao(eventoDto.getDescricao())
				.data(DateUtils.parseDate(eventoDto.getData())).dtAtualizacao(eventoDto.getDataAtualizacao())
				.endereco(eventoDto.getEndereco()).faixaKm(eventoDto.getFaixaKm()).gratuito(eventoDto.getGratuito())
				.valor(eventoDto.getValor())
				.urlSite(eventoDto.getUrlSite())
				.tipoEvento(tipoEventoService.loadTipoEvento(eventoDto.getIdTipoEvento()))
				.usuario(usuario).build());
	}

	private void sendWebSocketMessage(Usuario usuario, String action, String message) {
		EventoSocketMessageDTO messageDTO = EventoSocketMessageDTO.builder()
				.action(action)
				.message(message)
				.timestamp(LocalDateTime.now().toString())
				.userId(usuario.getId()).build();

		CustomWebSocketHandler.getSessions().parallelStream().forEach(session -> {
			if (session.isOpen()) {
				try {
					String json = ObjectMapperUtils.toJsonString(messageDTO);
					session.sendMessage(new TextMessage(json));
				} catch (IOException e) {
					throw new BikeIntegrationCustomException("Erro ao enviar mensagem para a sessão WebSocket: "
							+ session.getId() + " - " + e.getMessage());
				}
			}
		});
	}

	private List<Evento> getEventosProximosByLocation(Double latitude, Double longitude, Double raio) {
		return eventoRepository.findEventosProximosByLocation(latitude, longitude, raio);
	}
}
