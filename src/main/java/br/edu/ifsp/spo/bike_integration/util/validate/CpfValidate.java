package br.edu.ifsp.spo.bike_integration.util.validate;


import org.springframework.stereotype.Component;

import br.com.caelum.stella.validation.CPFValidator;
import br.edu.ifsp.spo.bike_integration.util.FormatUtils;

@Component
public interface CpfValidate {

	public record CpfValidationResult(boolean isValid, String message) {};
	
	CpfValidationResult CPF_LENGTH_ERROR = new CpfValidationResult(false, "CPF deve conter 11 caracteres");
	CpfValidationResult CPF_INVALID_ERROR = new CpfValidationResult(false, "CPF inválido");
	CpfValidationResult CPF_VALID = new CpfValidationResult(true, "CPF válido");

	static CpfValidationResult validate(String cpf) {
		cpf = FormatUtils.removeNonNumeric(cpf);

		if (!isCpfLengthValid(cpf)) {
			return CPF_LENGTH_ERROR;
		}
		if (!isCpfValid(cpf)) {
			return CPF_INVALID_ERROR;
		}
		return CPF_VALID;
	}

	// PRIVATE METHODS
	private static boolean isCpfLengthValid(String cpf) {
		return cpf.length() == 11;
	}

	private static boolean isCpfValid(String cpf) {
		CPFValidator cpfValidator = new CPFValidator();
		return cpfValidator.invalidMessagesFor(cpf).isEmpty();
	}
}
