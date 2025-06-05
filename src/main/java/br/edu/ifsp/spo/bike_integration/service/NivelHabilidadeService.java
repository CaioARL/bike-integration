package br.edu.ifsp.spo.bike_integration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.model.NivelHabilidade;
import br.edu.ifsp.spo.bike_integration.model.dto.NivelHabilidadeDTO;
import br.edu.ifsp.spo.bike_integration.repository.NivelHabilidadeRepository;

@Service
public class NivelHabilidadeService {

	@Autowired
	private NivelHabilidadeRepository nivelHabilidadeRepository;

	public List<NivelHabilidade> listarNiveisHabilidade() {
		return nivelHabilidadeRepository.findAll();
	}

	public NivelHabilidade cadastrarNivelHabilidade(NivelHabilidadeDTO nivelHabilidade) {
		return nivelHabilidadeRepository.save(NivelHabilidade.builder().nome(nivelHabilidade.getNome())
				.descricao(nivelHabilidade.getDescricao()).build());
	}

	public void buscarNivelHabilidade() {
		nivelHabilidadeRepository.findByNome(null);
	}

	public NivelHabilidade loadNivelHabilidade(Long id) {
		return nivelHabilidadeRepository.findById(id).orElse(null);
	}

}
