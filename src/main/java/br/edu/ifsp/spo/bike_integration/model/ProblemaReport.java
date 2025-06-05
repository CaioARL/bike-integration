package br.edu.ifsp.spo.bike_integration.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "problema_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemaReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private String usuarioId;

    @ManyToOne
    @JoinColumn(name = "id_problema", nullable = false)
    private Problema problema;

    @Column(name = "dt_criacao")
    private LocalDateTime dtCricao;

    @PrePersist
    void prePersist() {
        this.dtCricao = LocalDateTime.now();
    }

}
