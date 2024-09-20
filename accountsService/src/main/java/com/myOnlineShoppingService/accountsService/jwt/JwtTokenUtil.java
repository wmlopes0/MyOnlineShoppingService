package com.myOnlineShoppingService.accountsService.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 horas

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    // Genera un token de acceso JWT basado en un mapa genérico de claims
    public String generateAccessToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("com.myOnlineShoppingService")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }


    // Valida el token JWT
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token); // Validar firma y estructura
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expirado", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token es nulo, vacío o solo espacios en blanco", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT inválido", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT no soportado", ex);
        } catch (SignatureException ex) {
            LOGGER.error("Fallo en la validación de la firma");
        }
        return false;
    }

    // Extrae el sujeto (usuario) del token JWT
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    // Extrae el rol del usuario del token JWT
    public String getRole(String token) {
        return getClaims(token).get("role", String.class); // Devuelve el rol como String
    }

    // Extrae los claims (reclamaciones) del token JWT
    public Claims getClaims(String token) {
        return parseClaims(token);
    }

    // Parsea el token para extraer los claims
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
