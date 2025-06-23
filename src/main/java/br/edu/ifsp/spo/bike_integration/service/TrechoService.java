package br.edu.ifsp.spo.bike_integration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.model.Trecho;
import br.edu.ifsp.spo.bike_integration.repository.TrechoRepository;

@Service
public class TrechoService {

	@Autowired
	private TrechoRepository trechoRepository;

	public List<Trecho> findAll() {
		return trechoRepository.findAll();
	}

	public List<Trecho> findByInfraestruturaCicloviariaId(Long infraestruturaCicloviariaId) {
		return trechoRepository.findByInfraestruturaCicloviariaId(infraestruturaCicloviariaId);
	}

	public Trecho findById(Long id) {
		return trechoRepository.findById(id).orElse(null);
	}

	public Trecho findTrechoProximoByLocation(double latitude, double longitude, double raio) {
		return this.findTrechosProximosByLocation(latitude, longitude, raio)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public List<Trecho> findTrechosProximosByLocation(double latitude, double longitude, double raio) {
		return trechoRepository.findTrechosProximosByLocation(latitude, longitude, raio);
	}

	public Trecho findTrechoProximoByLocation(double latitude, double longitude) {
		return trechoRepository.findTrechoProximoByLocation(latitude, longitude);
	}

}
