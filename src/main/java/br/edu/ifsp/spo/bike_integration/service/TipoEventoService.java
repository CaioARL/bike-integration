package br.edu.ifsp.spo.bike_integration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.model.TipoEvento;
import br.edu.ifsp.spo.bike_integration.model.dto.TipoEventoDTO;
import br.edu.ifsp.spo.bike_integration.repository.TipoEventoRepository;

@Service
public class TipoEventoService {

	@Autowired
	private TipoEventoRepository tipoEventoRepository;

	@Autowired
	private NivelHabilidadeService nivelHabilidadeService;

	public List<TipoEvento> listarTiposEventos() {
		return tipoEventoRepository.findAll();
	}

	public TipoEvento cadastrarTipoEvento(TipoEventoDTO tipoEvento) {
		return tipoEventoRepository.save(TipoEvento.builder().nome(tipoEvento.getNome())
				.nivelHabilidade(nivelHabilidadeService.loadNivelHabilidade(tipoEvento.getNivelHabilidade())).build());
	}

	public TipoEvento loadTipoEvento(Long id) {
		return tipoEventoRepository.findById(id).orElse(null);
	}

}
