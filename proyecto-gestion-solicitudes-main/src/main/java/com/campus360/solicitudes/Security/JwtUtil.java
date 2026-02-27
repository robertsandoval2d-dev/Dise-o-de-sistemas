package com.campus360.solicitudes.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class JwtUtil {

    private final PublicKey publicKey;

    public JwtUtil(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Claims validarToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long obtenerIdUsuario(String token) {
        return validarToken(token).get("idUsuario", Long.class);
    }

    public String obtenerRol(String token) {
        return validarToken(token).get("rol", String.class);
    }

    public String obtenerNombre(String token) {
        return validarToken(token).get("nombre", String.class);
    }
}