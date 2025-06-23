package br.edu.ifsp.spo.bike_integration.interceptor;

import java.util.HashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.service.auth.JwtValidateService;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtValidateService jwtValidateService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Tenta obter o token do header Authorization ou do query param
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            String query = request.getURI().getQuery();
            if (query != null && query.startsWith("token=")) {
                token = query.replace("token=", "");
            }
        }

        if (token == null || !authenticateWithBearer(token)) {
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        // Não precisa implementar
    }

    /*
     * PRIVATE METHODS
     */
    private boolean authenticateWithBearer(String token) {
        if (token.isEmpty() || !this.jwtValidateService.isAuthenticated(token, RoleType.ADMIN)) {
            return false;
        }
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(this.jwtValidateService.getSubject(token), null,
                        new HashSet<>()));
        return true;
    }
}