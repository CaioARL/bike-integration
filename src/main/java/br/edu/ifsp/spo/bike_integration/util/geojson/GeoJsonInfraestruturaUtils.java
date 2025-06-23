package br.edu.ifsp.spo.bike_integration.util.geojson;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import br.edu.ifsp.spo.bike_integration.model.InfraestruturaCicloviaria;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO.FeatureDto;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO.GeometryDto;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO.PropertiesDto;
import br.edu.ifsp.spo.bike_integration.model.response.RouteResponseDTO;
import br.edu.ifsp.spo.bike_integration.util.GeoJsonUtils;

public class GeoJsonInfraestruturaUtils implements GeoJsonUtils<List<InfraestruturaCicloviaria>> {

	@Override
	public GeoJsonDTO convertToGeoJson(List<InfraestruturaCicloviaria> vias) {
		if (vias == null || vias.isEmpty()) {
			return GeoJsonDTO.builder().type("FeatureCollection").features(Collections.emptyList()).build();
		}
		List<FeatureDto> features = vias.stream().map(GeoJsonInfraestruturaUtils::createFeature)
				.collect(Collectors.toList());
		return GeoJsonDTO.builder().type("FeatureCollection").features(features).build();
	}

	public static GeoJsonDTO convertPolylineToGeoJson(RouteResponseDTO routeResponse) {
		if (routeResponse == null || routeResponse.getRoutes() == null) {
			return GeoJsonDTO.builder().type("FeatureCollection").features(Collections.emptyList()).build();
		}
		int[] stepId = { 0 };
		List<FeatureDto> features = routeResponse.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> leg.getSteps().stream())
				.map(step -> createFeatureFromStep(step, stepId[0]++))
				.collect(Collectors.toList());
		return GeoJsonDTO.builder().type("FeatureCollection").features(features).build();
	}

	public static GeoJsonDTO convertPolylineToSingleFeatureCollection(RouteResponseDTO routeResponse) {
		if (routeResponse == null || routeResponse.getRoutes() == null) {
			return GeoJsonDTO.builder().type("FeatureCollection").features(Collections.emptyList()).build();
		}
		List<List<Double>> allCoordinates = routeResponse.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> leg.getSteps().stream())
				.flatMap(step -> {
					List<List<Double>> coords = decodePolyline(step.getGeometry());
					return coords.size() > 1 ? coords.stream() : java.util.stream.Stream.empty();
				})
				.collect(Collectors.toList());

		if (allCoordinates.size() < 2) {
			return GeoJsonDTO.builder().type("FeatureCollection").features(Collections.emptyList()).build();
		}

		FeatureDto feature = FeatureDto.builder()
				.type("Feature")
				.id("0")
				.properties(PropertiesDto.builder()
						.name("Route")
						.id("0")
						.type("route")
						.build())
				.geometry(GeometryDto.builder()
						.type("LineString")
						.coordinates(allCoordinates)
						.build())
				.build();

		return GeoJsonDTO.builder().type("FeatureCollection").features(List.of(feature)).build();
	}

	/*
	 * PRIVATE METHODS
	 */

	private static FeatureDto createFeatureFromStep(RouteResponseDTO.StepDTO step, int id) {
		return FeatureDto.builder()
				.type("Feature")
				.id(String.valueOf(id))
				.properties(PropertiesDto.builder()
						.name(step.getName())
						.id(String.valueOf(id))
						.type(step.getMode())
						.maneuverType(step.getManeuver() != null && !"new name".equals(step.getManeuver().getType())
								? step.getManeuver().getType()
								: null)
						.maneuverModifier(
								step.getManeuver() != null && !"new name".equals(step.getManeuver().getModifier())
										? step.getManeuver().getModifier()
										: null)
						.build())
				.geometry(GeometryDto.builder()
						.type("LineString")
						.coordinates(decodePolyline(step.getGeometry()))
						.build())
				.build();
	}

	private static FeatureDto createFeature(InfraestruturaCicloviaria via) {
		PropertiesDto properties = PropertiesDto.builder()
				.name(via.getNome())
				.id(String.valueOf(via.getId()))
				.type(via.getTipoInfraestruturaCicloviaria().getNome())
				.notaMedia(via.getNotaMedia())
				.build();
		List<?> coordinates = via.getTrechos().stream()
				.map(trecho -> List.of(trecho.getLongitude(), trecho.getLatitude()))
				.collect(Collectors.toList());
		if ("Polygon".equals(via.getGeometria())) {
			coordinates = List.of(coordinates);
		}
		GeometryDto geometry = GeometryDto.builder().type(via.getGeometria()).coordinates(coordinates).build();
		return FeatureDto.builder().type("Feature").id(via.getJsonId()).properties(properties).geometry(geometry)
				.build();
	}

	// Decodifica polyline para lista de coordenadas [lon, lat]
	private static List<List<Double>> decodePolyline(String polyline) {
		if (polyline == null || polyline.isEmpty())
			return Collections.emptyList();
		List<List<Double>> coordinates = new java.util.ArrayList<>();
		int index = 0, len = polyline.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = polyline.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = polyline.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;
			coordinates.add(List.of(lng / 1E5, lat / 1E5));
		}
		return coordinates;
	}
}
