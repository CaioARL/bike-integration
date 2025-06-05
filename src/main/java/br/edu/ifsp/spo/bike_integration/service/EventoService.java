package br.edu.ifsp.spo.bike_integration.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifsp.spo.bike_integration.aws.service.S3Service;
import br.edu.ifsp.spo.bike_integration.dto.EventoDTO;
import br.edu.ifsp.spo.bike_integration.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.factory.GeoJsonUtilFactory;
import br.edu.ifsp.spo.bike_integration.hardcode.PaginationType;
import br.edu.ifsp.spo.bike_integration.model.Evento;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import br.edu.ifsp.spo.bike_integration.repository.EventoRepository;
import br.edu.ifsp.spo.bike_integration.response.ListEventoResponse;
import br.edu.ifsp.spo.bike_integration.rest.service.OpenStreetMapApiService;
import br.edu.ifsp.spo.bike_integration.util.DateUtils;
import br.edu.ifsp.spo.bike_integration.util.FormatUtils;
import br.edu.ifsp.spo.bike_integration.util.S3Utils;
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

	public Evento createEvento(EventoDTO eventoDto) {
		Map<String, Double> coordenadas = openStreetMapApiService
				.buscarCoordenadasPorEndereco(FormatUtils.formatEnderecoToOpenStreetMapApi(eventoDto.getEndereco()));
		eventoDto.getEndereco().setLatitude(coordenadas.get("lat"));
		eventoDto.getEndereco().setLongitude(coordenadas.get("lon"));

		Usuario usuario = usuarioService.loadUsuarioById(eventoDto.getIdUsuario());

		return eventoRepository.save(Evento.builder().nome(eventoDto.getNome()).descricao(eventoDto.getDescricao())
				.data(DateUtils.parseDate(eventoDto.getData())).dtAtualizacao(eventoDto.getDataAtualizacao())
				.endereco(eventoDto.getEndereco()).faixaKm(eventoDto.getFaixaKm()).gratuito(eventoDto.getGratuito())
				.urlSite(eventoDto.getUrlSite())
				.tipoEvento(tipoEventoService.loadTipoEvento(eventoDto.getIdTipoEvento()))
				.usuario(usuario).build());
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
			evento.setUrlSite(eventoDto.getUrlSite());
			evento.setUsuario(usuario);

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

	public void deleteEvento(Long id) {
		Evento evento = eventoRepository.findById(id).orElse(null);
		if (evento != null) {
			eventoRepository.delete(evento);
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

	private List<Evento> getEventosProximosByLocation(Double latitude, Double longitude, Double raio) {
		return eventoRepository.findEventosProximosByLocation(latitude, longitude, raio);
	}
}
