package br.edu.ifsp.spo.bike_integration.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.model.Trecho;
import br.edu.ifsp.spo.bike_integration.model.dto.GrafoTrechosDTO;

@Service
public class DijkstraService {
    public List<Trecho> encontrarRotaDijkstra(Trecho origem, Trecho destino, GrafoTrechosDTO grafo) {
        Map<Trecho, Double> distancias = new HashMap<>();
        Map<Trecho, Trecho> anteriores = new HashMap<>();
        PriorityQueue<Trecho> fila = new PriorityQueue<>(Comparator.comparing(distancias::get));

        for (Trecho t : grafo.getTrechos()) {
            distancias.put(t, Double.POSITIVE_INFINITY);
            anteriores.put(t, null);
        }
        distancias.put(origem, 0.0);
        fila.add(origem);

        while (!fila.isEmpty()) {
            Trecho atual = fila.poll();
            if (atual.equals(destino))
                break;

            for (Trecho vizinho : grafo.getVizinhos(atual)) {
                double peso = calcularDistancia(atual, vizinho); // Implemente este método
                double novaDist = distancias.get(atual) + peso;
                if (novaDist < distancias.get(vizinho)) {
                    distancias.put(vizinho, novaDist);
                    anteriores.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }

        // Reconstruir o caminho
        LinkedList<Trecho> caminho = new LinkedList<>();
        Trecho passo = destino;
        while (passo != null) {
            caminho.addFirst(passo);
            passo = anteriores.get(passo);
        }
        if (caminho.getFirst().equals(origem)) {
            return caminho;
        } else {
            return Collections.emptyList(); // Sem caminho
        }
    }

    // Exemplo de cálculo de distância (pode ser melhorado)
    private double calcularDistancia(Trecho a, Trecho b) {
        double lat1 = a.getLatitude();
        double lon1 = a.getLongitude();
        double lat2 = b.getLatitude();
        double lon2 = b.getLongitude();
        // Distância Euclidiana simples
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2));
    }
}
