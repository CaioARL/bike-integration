package br.edu.ifsp.spo.bike_integration.util.geojson;

import java.util.List;

import br.edu.ifsp.spo.bike_integration.model.Trecho;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.util.GeoJsonUtils;

public class GeoJsonTrechoUtils implements GeoJsonUtils<List<Trecho>> {

    @Override
    public GeoJsonDTO convertToGeoJson(List<Trecho> trechos) {
        // Cria um único FeatureDto do tipo LineString com todos os pontos
        List<List<Double>> coordinates = trechos.stream()
                .map(t -> List.of(t.getLongitude(), t.getLatitude()))
                .toList();

        GeoJsonDTO.GeometryDto geometry = GeoJsonDTO.GeometryDto.builder()
                .type("LineString")
                .coordinates(coordinates)
                .build();

        GeoJsonDTO.PropertiesDto properties = GeoJsonDTO.PropertiesDto.builder()
                .type("Trecho")
                .build();

        GeoJsonDTO.FeatureDto feature = GeoJsonDTO.FeatureDto.builder()
                .type("Feature")
                .properties(properties)
                .geometry(geometry)
                .build();

        return GeoJsonDTO.builder()
                .type("FeatureCollection")
                .features(List.of(feature))
                .build();
    }

}
