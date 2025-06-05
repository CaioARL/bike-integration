package br.edu.ifsp.spo.bike_integration.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.dto.AvaliacaoDTO;
import br.edu.ifsp.spo.bike_integration.hardcode.PaginationType;
import br.edu.ifsp.spo.bike_integration.model.AvaliacaoInfraestruturaCicloviaria;
import br.edu.ifsp.spo.bike_integration.repository.AvaliacaoInfraestruturaCicloviariaRepository;
import br.edu.ifsp.spo.bike_integration.response.AvaliacaoDetailResponse;
import br.edu.ifsp.spo.bike_integration.response.ListAvaliacoesResponse;

@Service
public class AvaliacaoInfraestruturaCicloviariaService {

	@Autowired
	private AvaliacaoInfraestruturaCicloviariaRepository repository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private InfraestruturaCicloviariaService infraestruturaService;

	public Integer avaliar(AvaliacaoDTO avaliacao) {

		// Pre save
		AvaliacaoInfraestruturaCicloviaria avaliacaoToSave = AvaliacaoInfraestruturaCicloviaria.builder()
				.usuario(usuarioService.loadUsuarioById(avaliacao.getIdUsuario()))
				.infraestruturaCicloviaria(infraestruturaService.findById(avaliacao.getIdInfraestruturaCicloviaria()))
				.nota(avaliacao.getNota()).comentario(avaliacao.getComentario()).dtCriacao(LocalDateTime.now()).build();

		// Add pre save to others
		List<AvaliacaoInfraestruturaCicloviaria> infraestruturas = repository
				.findAllByInfraestruturaCicloviariaId(avaliacao.getIdInfraestruturaCicloviaria());
		infraestruturas.add(avaliacaoToSave);

		// Save
		repository.save(avaliacaoToSave);

		return (int) infraestruturas.stream().mapToDouble(AvaliacaoInfraestruturaCicloviaria::getNota).average()
				.orElse(0.0);

	}

	public ListAvaliacoesResponse getAllByInfraestruturaCicloviariaIdAndNota(Long idInfraestruturaCicloviaria,
			Integer nota,
			Integer pagina) {
		Long limit = PaginationType.RESULTS_PER_PAGE.getValue();

		Long offset = (pagina - 1) * limit;

		List<AvaliacaoInfraestruturaCicloviaria> avaliacoes = repository
				.findAllByInfraestruturaCicloviariaIdAndNota(idInfraestruturaCicloviaria, nota, limit, offset);

		Long count = repository
				.countByInfraestruturaCicloviariaIdAndNota(idInfraestruturaCicloviaria, nota);

		Long totalPaginas = (long) Math.ceil(count / (double) limit);

		return ListAvaliacoesResponse.builder().avaliacoes(avaliacoes).totalRegistros(count)
				.totalPaginas(totalPaginas).build();
	}

	public AvaliacaoDetailResponse getDetails(Long idInfraestruturaCicloviaria) {
		Long countAvaliacao = repository.countByInfraestruturaCicloviariaId(idInfraestruturaCicloviaria);

		Map<String, Long> mapAvaliacoes = repository
				.findAvaliacoesDetailsByInfraestruturaCicloviariaId(idInfraestruturaCicloviaria).stream()
				.sorted((a, b) -> Integer.compare(Integer.parseInt(a[0].toString()), Integer.parseInt(b[0].toString())))
				.collect(Collectors.toMap(
						obj -> String.format("Nota %s", obj[0]),
						obj -> (Long) obj[1],
						(e1, e2) -> e1,
						LinkedHashMap::new));

		return AvaliacaoDetailResponse.builder().countAvaliacao(countAvaliacao).avaliacoes(mapAvaliacoes).build();
	}

}
