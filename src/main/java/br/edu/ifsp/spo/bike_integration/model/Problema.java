package br.edu.ifsp.spo.bike_integration.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "problema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "report_count", nullable = false)
    private Integer reportCount;

    @Column(name = "s3_url")
    private String s3Url;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trecho", nullable = false)
    private Trecho trecho;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_problema", nullable = false)
    private TipoProblema tipoProblema;

    @JsonIgnore
    @OneToMany(mappedBy = "problema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemaReport> reports;

    @PrePersist
    public void prePersist() {
        this.dtCriacao = LocalDateTime.now();
    }
}