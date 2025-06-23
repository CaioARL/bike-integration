package br.edu.ifsp.spo.bike_integration.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrasilApiCnpjResponse {

    private String uf;

    private String cep;

    private List<Qsa> qsa;

    private String cnpj;

    private String pais;

    private String email;

    private String porte;

    private String bairro;

    private String numero;

    @JsonProperty("ddd_fax")
    private String dddFax;

    private String municipio;

    private String logradouro;

    @JsonProperty("cnae_fiscal")
    private Long cnaeFiscal;

    @JsonProperty("codigo_pais")
    private String codigoPais;

    private String complemento;

    @JsonProperty("codigo_porte")
    private Long codigoPorte;

    @JsonProperty("razao_social")
    private String razaoSocial;

    @JsonProperty("nome_fantasia")
    private String nomeFantasia;

    @JsonProperty("capital_social")
    private Long capitalSocial;

    @JsonProperty("ddd_telefone_1")
    private String dddTelefone1;

    @JsonProperty("ddd_telefone_2")
    private String dddTelefone2;

    @JsonProperty("opcao_pelo_mei")
    private String opcaoPeloMei;

    @JsonProperty("descricao_porte")
    private String descricaoPorte;

    @JsonProperty("codigo_municipio")
    private Long codigoMunicipio;

    @JsonProperty("cnaes_secundarios")
    private List<CnaesSecundarios> cnaesSecundarios;

    @JsonProperty("natureza_juridica")
    private String naturezaJuridica;

    @JsonProperty("situacao_especial")
    private String situacaoEspecial;

    @JsonProperty("opcao_pelo_simples")
    private String opcaoPeloSimples;

    @JsonProperty("situacao_cadastral")
    private Long situacaoCadastral;

    @JsonProperty("data_opcao_pelo_mei")
    private String dataOpcaoPeloMei;

    @JsonProperty("data_exclusao_do_mei")
    private String dataExclusaoDoMei;

    @JsonProperty("cnae_fiscal_descricao")
    private String cnaeFiscalDescricao;

    @JsonProperty("codigo_municipio_ibge")
    private Long codigoMunicipioIbge;

    @JsonProperty("data_inicio_atividade")
    private String dataInicioAtividade;

    @JsonProperty("data_situacao_especial")
    private String dataSituacaoEspecial;

    @JsonProperty("data_opcao_pelo_simples")
    private String dataOpcaoPeloSimples;

    @JsonProperty("data_situacao_cadastral")
    private String dataSituacaoCadastral;

    @JsonProperty("nome_cidade_no_exterior")
    private String nomeCidadeNoExterior;

    @JsonProperty("codigo_natureza_juridica")
    private Long codigoNaturezaJuridica;

    @JsonProperty("data_exclusao_do_simples")
    private String dataExclusaoDoSimples;

    @JsonProperty("motivo_situacao_cadastral")
    private Long motivoSituacaoCadastral;

    @JsonProperty("ente_federativo_responsavel")
    private String enteFederativoResponsavel;

    @JsonProperty("identificador_matriz_filial")
    private Long identificadorMatrizFilial;

    @JsonProperty("qualificacao_do_responsavel")
    private Long qualificacaoDoResponsavel;

    @JsonProperty("descricao_situacao_cadastral")
    private String descricaoSituacaoCadastral;

    @JsonProperty("descricao_tipo_de_logradouro")
    private String descricaoTipoDeLogradouro;

    @JsonProperty("descricao_motivo_situacao_cadastral")
    private String descricaoMotivoSituacaoCadastral;

    @JsonProperty("descricao_identificador_matriz_filial")
    private String descricaoIdentificadorMatrizFilial;

    @Getter
    @Setter
    public static class Qsa {
        private String pais;

        @JsonProperty("nome_socio")
        private String nomeSocio;

        @JsonProperty("codigo_pais")
        private String codigoPais;

        @JsonProperty("faixa_etaria")
        private String faixaEtaria;

        @JsonProperty("cnpj_cpf_do_socio")
        private String cnpjCpfDoSocio;

        @JsonProperty("qualificacao_socio")
        private String qualificacaoSocio;

        @JsonProperty("codigo_faixa_etaria")
        private Long codigoFaixaEtaria;

        @JsonProperty("data_entrada_sociedade")
        private String dataEntradaSociedade;

        @JsonProperty("identificador_de_socio")
        private Long identificadorDeSocio;

        @JsonProperty("cpf_representante_legal")
        private String cpfRepresentanteLegal;

        @JsonProperty("nome_representante_legal")
        private String nomeRepresentanteLegal;

        @JsonProperty("codigo_qualificacao_socio")
        private Long codigoQualificacaoSocio;

        @JsonProperty("qualificacao_representante_legal")
        private String qualificacaoRepresentanteLegal;

        @JsonProperty("codigo_qualificacao_representante_legal")
        private Long codigoQualificacaoRepresentanteLegal;
    }

    @Getter
    @Setter
    public static class CnaesSecundarios {

        private Long codigo;

        private String descricao;
    }
}
