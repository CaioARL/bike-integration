package br.edu.ifsp.spo.bike_integration.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateTokenResponse {

    private Boolean success;
    private String token;

}
