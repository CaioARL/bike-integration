package br.edu.ifsp.spo.bike_integration.model.response;

import java.util.List;

import br.edu.ifsp.spo.bike_integration.model.Evento;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListEventoResponse {

	List<Evento> eventos;
	Long totalRegistros;
	Long totalPaginas;
}
