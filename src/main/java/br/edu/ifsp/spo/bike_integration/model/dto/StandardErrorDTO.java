package br.edu.ifsp.spo.bike_integration.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StandardErrorDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Builder.Default
	private Integer status = HttpStatus.INTERNAL_SERVER_ERROR.value();

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String path;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String error;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String message;

}