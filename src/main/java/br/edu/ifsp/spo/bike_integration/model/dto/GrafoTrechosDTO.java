package br.edu.ifsp.spo.bike_integration.model.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.edu.ifsp.spo.bike_integration.model.Trecho;

public class GrafoTrechosDTO {
    private final Map<Trecho, List<Trecho>> adjacencias = new HashMap<>();

    public void adicionarTrecho(Trecho trecho) {
        adjacencias.putIfAbsent(trecho, new ArrayList<>());
    }

    public void conectarTrechos(Trecho a, Trecho b) {
        adjacencias.get(a).add(b);
        adjacencias.get(b).add(a); // Se for bidirecional
    }

    public List<Trecho> getVizinhos(Trecho trecho) {
        return adjacencias.getOrDefault(trecho, new ArrayList<>());
    }

    public Set<Trecho> getTrechos() {
        return adjacencias.keySet();
    }
}
