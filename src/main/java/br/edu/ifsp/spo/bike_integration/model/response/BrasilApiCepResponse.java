package br.edu.ifsp.spo.bike_integration.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrasilApiCepResponse {

    private String cep;
    private String state;
    private String city;
    private String neighborhood;
    private String street;

    @JsonIgnore
    private String service;

    private Location location;

    // Getters e Setters

    @Getter
    @Setter
    @Builder
    public static class Location {
        private String type;
        private Coordinates coordinates;

        @Getter
        @Setter
        @AllArgsConstructor
        @Builder
        public static class Coordinates {
            private Double longitude;
            private Double latitude;
        }

    }
}
