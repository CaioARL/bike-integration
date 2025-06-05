package br.edu.ifsp.spo.bike_integration.util;

import java.net.URLConnection;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.model.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface ResponseUtils {

	Logger LOGGER = LoggerFactory.getLogger(ResponseUtils.class);

	/**
	 * buildBinaryResponse
	 * 
	 * @param s3Key
	 * @param fileBytes
	 * @param inline
	 * @return ResponseEntity<byte[]>
	 */
	public static ResponseEntity<byte[]> buildBinaryResponse(String s3Key, byte[] fileBytes, boolean inline) {
		String mimeType = URLConnection.guessContentTypeFromName(s3Key);
		if (mimeType == null) {
			mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		String disposition = inline ? "inline" : "attachment";
		return ResponseEntity.ok()
				.header("Content-Disposition", disposition + "; filename=\"" + s3Key + "\"")
				.contentType(MediaType.parseMediaType(mimeType))
				.body(fileBytes);
	}

	/**
	 *
	 * createResponse
	 *
	 *
	 */
	public static ResponseEntity<Object> createResponse(ErrorResponse errorResponse, HttpStatus badRequest) {
		return new ResponseEntity<>(createMap(errorResponse.getMessage(), badRequest), badRequest);
	}

	/**
	 *
	 * putJsonOnResponse
	 *
	 *
	 */
	public static void putJsonOnResponse(HttpServletResponse response, Object object, HttpStatus httpStatus)
			throws BikeIntegrationCustomException {
		putJsonOnResponse(response, ObjectMapperUtils.toJsonString(object), httpStatus);
	}

	public static void putJsonOnResponse(HttpServletResponse response, String json, HttpStatus httpStatus)
			throws BikeIntegrationCustomException {
		try {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Charset.forName("UTF-8").name());
			if (StringUtils.isNotBlank(json)) {
				response.getWriter().write(json);
			}
			response.setStatus(httpStatus.value());
		} catch (Exception e) {
			throw new BikeIntegrationCustomException("Erro ao escrever resposta JSON", e);
		}
	}

	/**
	 *
	 * getCurrent
	 *
	 *
	 */
	public static HttpServletResponse getCurrent() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return (attributes != null) ? attributes.getResponse() : null;
	}

	/**
	 *
	 * PRIVATE METHODS
	 *
	 *
	 */
	private static Map<String, Object> createMap(String message, HttpStatus status) {
		return Map.of("status", status.value(), "timestamp", LocalDateTime.now(), "message", reduceMessage(message));
	}

	public static String reduceMessage(String message) {
		if (message == null) {
			return null;
		}
		int maxLength = 100;
		if (message.length() <= maxLength) {
			return message;
		}
		return message.substring(0, maxLength) + "...";
	}

}