package br.edu.ifsp.spo.bike_integration.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvaliacaoDetailResponse {

    @JsonProperty("total_avaliacoes")
    private Long countAvaliacao;

    @JsonProperty("total_avaliacoes_por_nota")
    private Map<String, Long> avaliacoes;

}
