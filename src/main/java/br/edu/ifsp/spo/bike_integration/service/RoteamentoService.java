package br.edu.ifsp.spo.bike_integration.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.factory.GeoJsonUtilFactory;
import br.edu.ifsp.spo.bike_integration.model.InfraestruturaCicloviaria;
import br.edu.ifsp.spo.bike_integration.model.Trecho;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.model.dto.GrafoTrechosDTO;

@Service
public class RoteamentoService {

    @Autowired
    private InfraestruturaCicloviariaService infraestruturaCicloviariaService;

    @Autowired
    private TrechoService trechoService;

    @Autowired
    private DijkstraService dijkstraService;

    public GeoJsonDTO encontrarRotaGeoJson(double origemLat, double origemLng, double destinoLat, double destinoLng) {
        try {
            Trecho origem = trechoService.findTrechoProximoByLocation(origemLat, origemLng);
            Trecho destino = trechoService.findTrechoProximoByLocation(destinoLat, destinoLng);
            if (origem == null) {
                throw new BikeIntegrationCustomException("Origem não encontrada próximo às coordenadas informadas.");
            }
            if (destino == null) {
                origem = trechoService.findTrechosProximosByLocation(origemLat, origemLng, 1000).stream()
                        .findFirst()
                        .orElseThrow(() -> new BikeIntegrationCustomException(
                                "Destino não encontrado próximo às coordenadas informadas."));
            }
            GrafoTrechosDTO grafo = construirGrafoDosTrechos(origemLat, origemLng, destino.getLatitude(),
                    destino.getLongitude());
            List<Trecho> rota = dijkstraService.encontrarRotaDijkstra(origem, destino, grafo);
            return GeoJsonUtilFactory.convertTrechosToGeoJson(rota);
        } catch (Exception e) {
            throw new BikeIntegrationCustomException("Erro ao encontrar rota: " + e.getMessage(), e);
        }
    }

    /*
     * PRIVATE METHODS
     */

    private double calcularDistanciaHaversine(double lat1, double lng1, double lat2, double lng2) {
        double raioTerra = 6371000.0; // metros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return raioTerra * c;
    }

    private double calcularRaioBusca(double origemLat, double origemLng, double destinoLat, double destinoLng) {
        double distancia = calcularDistanciaHaversine(origemLat, origemLng, destinoLat, destinoLng);
        return distancia + 20.0; // margem extra
    }

    private Set<InfraestruturaCicloviaria> buscarInfraestruturasProximas(double origemLat, double origemLng,
            double destinoLat, double destinoLng, double raioMetros) {
        List<InfraestruturaCicloviaria> infraProximasOrigem = infraestruturaCicloviariaService
                .findInfraestruturasProximasByLocation(
                        origemLat,
                        origemLng,
                        raioMetros);
        List<InfraestruturaCicloviaria> infraProximasDestino = infraestruturaCicloviariaService
                .findInfraestruturasProximasByLocation(
                        destinoLat,
                        destinoLng,
                        raioMetros);
        Set<InfraestruturaCicloviaria> infraestruturas = new HashSet<>();
        infraestruturas.addAll(infraProximasOrigem);
        infraestruturas.addAll(infraProximasDestino);
        return infraestruturas;
    }

    private void adicionarTrechosEConexoes(GrafoTrechosDTO grafo,
            Set<InfraestruturaCicloviaria> infraestruturas) {
        for (InfraestruturaCicloviaria infraestrutura : infraestruturas) {
            List<Trecho> trechos = infraestrutura.getTrechos();
            trechos.sort((t1, t2) -> {
                int cmp = Double.compare(t1.getLatitude(), t2.getLatitude());
                if (cmp == 0) {
                    cmp = Double.compare(t1.getLongitude(), t2.getLongitude());
                }
                return cmp;
            });
            for (Trecho trecho : trechos) {
                grafo.adicionarTrecho(trecho);
            }
            for (int i = 0; i < trechos.size() - 1; i++) {
                Trecho atual = trechos.get(i);
                Trecho proximo = trechos.get(i + 1);
                grafo.conectarTrechos(atual, proximo);
            }
        }
    }

    private void conectarCruzamentosEmParalelo(GrafoTrechosDTO grafo,
            Set<InfraestruturaCicloviaria> infraestruturas) {
        List<Trecho> todosTrechos = new ArrayList<>();
        for (InfraestruturaCicloviaria infra : infraestruturas) {
            todosTrechos.addAll(infra.getTrechos());
        }
        double tolerancia = 0.00001; // ~1 metro
        todosTrechos.parallelStream().forEach(t1 -> {
            todosTrechos.stream().forEach(t2 -> {
                if (!t1.equals(t2) && !t1.getInfraestruturaCicloviaria().getId()
                        .equals(t2.getInfraestruturaCicloviaria().getId())) {
                    if (Math.abs(t1.getLatitude() - t2.getLatitude()) < tolerancia &&
                            Math.abs(t1.getLongitude() - t2.getLongitude()) < tolerancia) {
                        synchronized (grafo) {
                            grafo.conectarTrechos(t1, t2);
                        }
                    }
                }
            });
        });
    }

    private GrafoTrechosDTO construirGrafoDosTrechos(double origemLat, double origemLng, double destinoLat,
            double destinoLng) {
        double raioMetros = calcularRaioBusca(origemLat, origemLng, destinoLat, destinoLng);
        Set<InfraestruturaCicloviaria> infraestruturas = buscarInfraestruturasProximas(origemLat, origemLng,
                destinoLat, destinoLng, raioMetros);
        GrafoTrechosDTO grafo = new GrafoTrechosDTO();
        adicionarTrechosEConexoes(grafo, infraestruturas);
        conectarCruzamentosEmParalelo(grafo, infraestruturas);
        return grafo;
    }
}
