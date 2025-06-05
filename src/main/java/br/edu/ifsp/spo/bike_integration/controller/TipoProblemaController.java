package br.edu.ifsp.spo.bike_integration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.annotation.XAccessKey;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.TipoProblema;
import br.edu.ifsp.spo.bike_integration.service.TipoProblemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/tipo/problema")
@Tag(name = "Tipo Problema", description = "Controller para operações relacionadas a tipos de problemas.")
public class TipoProblemaController {

    @Autowired
    private TipoProblemaService tipoProblemaService;

    @Role({RoleType.PF, RoleType.PJ, RoleType.ADMIN})
    @BearerToken
    @XAccessKey
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retorna todos os tipos de problemas cadastrados.")
    public ResponseEntity<List<TipoProblema>> listarTiposProblemas() {
        return ResponseEntity.ok(tipoProblemaService.listarTiposProblemas());
    }
}