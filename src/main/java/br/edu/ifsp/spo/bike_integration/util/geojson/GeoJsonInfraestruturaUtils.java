package br.edu.ifsp.spo.bike_integration.util.geojson;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import br.edu.ifsp.spo.bike_integration.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.dto.GeoJsonDTO.FeatureDto;
import br.edu.ifsp.spo.bike_integration.dto.GeoJsonDTO.GeometryDto;
import br.edu.ifsp.spo.bike_integration.dto.GeoJsonDTO.PropertiesDto;
import br.edu.ifsp.spo.bike_integration.model.InfraestruturaCicloviaria;
import br.edu.ifsp.spo.bike_integration.util.GeoJsonUtils;

public class GeoJsonInfraestruturaUtils implements GeoJsonUtils<List<InfraestruturaCicloviaria>> {

	@Override
	public GeoJsonDTO convertToGeoJson(List<InfraestruturaCicloviaria> vias) {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		try {
			Future<List<FeatureDto>> future = executorService
					.submit(() -> vias.parallelStream().map(this::createFeature).toList());
			return GeoJsonDTO.builder()
					.type("FeatureCollection")
					.features(future.get())
					.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			executorService.shutdown();
		}
	}

	/*
	 * PRIVATE METHODS
	 */

	private FeatureDto createFeature(InfraestruturaCicloviaria via) {
		// Properties
		PropertiesDto properties = PropertiesDto.builder().name(via.getNome()).id(String.valueOf(via.getId()))
				.type(via.getTipoInfraestruturaCicloviaria().getNome()).notaMedia(via.getNotaMedia()).build();

		// Coordinates
		List<?> coordinates = via.getTrechos().stream()
				.map(trecho -> List.of(trecho.getLongitude(), trecho.getLatitude())).toList();

		if (via.getGeometria().equals("Polygon")) {
			coordinates = List.of(coordinates);
		}

		// Geometry
		GeometryDto geometry = GeometryDto.builder().type(via.getGeometria()).coordinates(coordinates).build();

		// Feature
		return FeatureDto.builder().type("Feature").id(via.getJsonId()).properties(properties).geometry(geometry)
				.build();
	}

}
