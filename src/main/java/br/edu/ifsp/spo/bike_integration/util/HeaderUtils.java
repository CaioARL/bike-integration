package br.edu.ifsp.spo.bike_integration.util;

import jakarta.servlet.http.HttpServletRequest;

public interface HeaderUtils {

	public static String getAccessToken(HttpServletRequest request) {
		return request.getHeader("access-token");
	}

	public static String getBearerToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}

}
