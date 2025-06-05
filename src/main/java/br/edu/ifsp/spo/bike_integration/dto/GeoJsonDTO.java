package br.edu.ifsp.spo.bike_integration.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GeoJsonDTO {
	private String type;
	private List<FeatureDto> features;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class FeatureDto {
		private String type;
		private String id;
		private PropertiesDto properties;
		private GeometryDto geometry;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class PropertiesDto {
		private String name;
		private String id;
		private String type;
		private Double notaMedia;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class GeometryDto {
		private String type;
		private List<?> coordinates;

		public void setCoordinatesForPoint(List<Double> point) {
			this.coordinates = point;
		}

		public void setCoordinatesForLineString(List<List<Double>> lineString) {
			this.coordinates = lineString;
		}

		public void setCoordinatesForPolygon(List<List<List<Double>>> polygon) {
			this.coordinates = polygon;
		}
	}

}
