package com.VitaTrack.Backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Component
public class WebSocketAuthHandshakeHandler implements HandshakeInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        var uri = request.getURI();
        var q = uri.getQuery();
        if (q != null && q.contains("token=")) {
            String token = null;
            for (String part : q.split("&")) {
                if (part.startsWith("token=")) { token = part.substring(6); break; }
            }
            if (token != null) {
                try {
                    Jws<Claims> claims = jwtUtil.validateToken(token);
                    String username = claims.getBody().getSubject();
                    Principal p = () -> username;
                    attributes.put("principal", p);
                    return true;
                } catch (JwtException ex) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
