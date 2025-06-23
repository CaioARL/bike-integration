package br.edu.ifsp.spo.bike_integration.util;

import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO;

public interface GeoJsonUtils<T> {
	public GeoJsonDTO convertToGeoJson(T object);
}
