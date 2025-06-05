package br.edu.ifsp.spo.bike_integration.model.response;

import java.util.List;

import br.edu.ifsp.spo.bike_integration.model.AvaliacaoInfraestruturaCicloviaria;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListAvaliacoesResponse {

    List<AvaliacaoInfraestruturaCicloviaria> avaliacoes;
    Long totalRegistros;
    Long totalPaginas;
}
