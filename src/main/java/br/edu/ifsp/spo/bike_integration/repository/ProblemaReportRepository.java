package br.edu.ifsp.spo.bike_integration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifsp.spo.bike_integration.model.ProblemaReport;

public interface ProblemaReportRepository extends JpaRepository<ProblemaReport, Long> {

    boolean existsByUsuarioIdAndProblemaId(String usuarioId, Long problemaId);

}