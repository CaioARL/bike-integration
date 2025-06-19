package br.edu.ifsp.spo.bike_integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.dto.GeoJsonDTO;
import br.edu.ifsp.spo.bike_integration.rest.service.OpenStreetMapApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/rota")
@Tag(name = "Rota", description = "Controller para operações relacionadas a rota.")
public class RotaController {

    @Autowired
    private OpenStreetMapApiService openStreetMapApiService;

    @Role({ RoleType.PF, RoleType.ADMIN })
    @BearerToken
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calcula a rota entre pontos.")
    public ResponseEntity<GeoJsonDTO> calcularRota(@RequestParam String coordenadas) {
        return ResponseEntity.ok(openStreetMapApiService.buscarRotaPorString(coordenadas));
    }

}
