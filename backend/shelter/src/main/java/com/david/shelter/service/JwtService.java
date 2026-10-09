package com.david.shelter.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import com.david.shelter.model.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.david.shelter.dto.AuthenticationDTO;
import com.david.shelter.exception.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Este Servicio es el que se encarga de crear y verificar el JSON Web Token (JWT), tambien de obtener el email de usuario a partir del token, esto
// es utiil para inyectarlo en el SEcurityContextHolder en el JwtFilter
@Service 
public class JwtService {

    // Semilla que se usa para la encriptacion, inyectamos el valor desde el application.properties
    @Value("${CRYPTO_SEED}")
    private String CRYPTO_SEED;
    // Expiracion del token, en horas
    @Value("${JWT_EXPIRATION}")
    private Integer hours;

    // Generar token
    public String generateToken(AuthenticationDTO auth) {

        // Extraemos el usuario
        String user = auth.user();

        // Extraemos los roles (claims), en este caso es solo uno, y lo convertimos a String
        String role = auth.rol().toString();

        return Jwts.builder()
                .subject(user)
                .claim("role", role)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofHours(hours))))
                .signWith(Keys.hmacShaKeyFor(CRYPTO_SEED.getBytes()))
                .compact();

    }

    // Sacar el email del usuario a partir del token lo vamos a necesitar en el filtro para inyectar el el SecurityContextHolder
    public String getUserFromToken(String token) {

        try {
            return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(CRYPTO_SEED.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
        } catch (Exception e) {
            throw new InvalidTokenException("Error obteniendo el nombre de usuario a partir del token");
        }
    }

    // Extraemos el Rol del token, tambien es necesareo para el filtro
    public Role getRoleFromToken(String token) {

        try {
            // usamos el valueOf porque el get del parser no me permite devolver directamente el Enum
            return Role.valueOf(Jwts.parser()
                       .verifyWith(Keys.hmacShaKeyFor(CRYPTO_SEED.getBytes()))
                       .build()
                       .parseSignedClaims(token)
                       .getPayload()
                       .get("role",String.class).toUpperCase());
        } catch (Exception e) {
            throw new InvalidTokenException("No se ha podido extraer el rol de usuario del token");
        }
    }

    // Verificar la validez de un token
    public boolean verifyToken(String token) {

        try {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(CRYPTO_SEED.getBytes()))
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
