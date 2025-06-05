
package br.edu.ifsp.spo.bike_integration.model.dto;

import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class EnderecoDTO {

    @Schema(example = "89010025")
    private String cep;

    @Schema(example = "SC")
    private String estado;

    @Schema(example = "Blumenau")
    private String cidade;

    @Schema(example = "Centro")
    private String bairro;

    @Schema(example = "Rua Doutor Luiz de Freitas Melro")
    private String rua;

    @Schema(example = "123")
    private Long numero;

    @Hidden
    private Double latitude;

    @Hidden
    private Double longitude;
}
