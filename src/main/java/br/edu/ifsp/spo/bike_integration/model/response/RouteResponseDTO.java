package br.edu.ifsp.spo.bike_integration.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteResponseDTO {
    private String code;
    private List<RouteDTO> routes;
    private List<WaypointDTO> waypoints;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RouteDTO {
        private List<LegDTO> legs;
        private Double weight;
        private String summary;
        private Double duration;
        private Double distance;
        private String weight_name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LegDTO {
        private List<StepDTO> steps;
        private Double weight;
        private String summary;
        private Double duration;
        private Double distance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class StepDTO {
        private List<IntersectionDTO> intersections;
        private String driving_side;
        private String geometry;
        private ManeuverDTO maneuver;
        private String name;
        private String mode;
        private Double weight;
        private Double duration;
        private Double distance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class IntersectionDTO {
        private Integer out;
        @JsonProperty("in")
        private Integer inValue;
        private List<Boolean> entry;
        private List<Integer> bearings;
        private List<Double> location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ManeuverDTO {
        private Integer bearing_after;
        private Integer bearing_before;
        private List<Double> location;
        private String modifier;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class WaypointDTO {
        private String hint;
        private List<Double> location;
        private String name;
        private Double distance;
    }
}