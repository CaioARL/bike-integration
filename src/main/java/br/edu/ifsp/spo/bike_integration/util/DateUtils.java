package br.edu.ifsp.spo.bike_integration.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;

public interface DateUtils {

	public static String getFormattedHyphenStringDate(LocalDateTime date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public static String getFormattedSlashStringDate(LocalDateTime date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(date);
	}

	public static LocalDateTime parseDate(String date) {
		try {
			return LocalDateTime.parse(date);
		} catch (Exception e) {
			throw new BikeIntegrationCustomException("Error parsing date", e);
		}
	}

	public static String fixFormattDate(String date) {
		if (date == null) {
			return null;
		}
		if (date.contains("/")) {
			return fixDateHyphen(date);
		}
		// Se vier no formato yyyy-MM-ddTHH:mm ou yyyy-MM-ddTHH:mm:ss, retorna como está
		if (date.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?")) {
			return date;
		}
		return date;
	}

	/*
	 * PRIVATE METHODS
	 */

	private static String fixDateHyphen(String date) {
		String[] dateSplited = date.split("/");
		return dateSplited[2] + "-" + dateSplited[1] + "-" + dateSplited[0];
	}

}
