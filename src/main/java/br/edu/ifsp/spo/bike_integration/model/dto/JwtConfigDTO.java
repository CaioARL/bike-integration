package br.edu.ifsp.spo.bike_integration.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JwtConfigDTO {

    private String accessKey;
    private String secretKey;
    private Long expiration;
}