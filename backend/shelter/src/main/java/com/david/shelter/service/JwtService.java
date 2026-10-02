package com.david.shelter.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

// Este Servicio es el que se encarga de crear y verificar el JSON Web Token (JWT)
@Service 
public class JwtService {

    // Semilla que se usa para la encriptacion 
    private final String CRYPTO_SEED = "huuiUHuiHibyuf598y6FYHGbjhuygt45huHUG0hug6f5d3ss23Dgjllpppin9IjhBHbk";
    private final Integer JWT_EXPIRATION = 86400000; // 24 horas en milisegundos

    // Generar token
    public String generateToken(Authentication auth) {
        
        return "";
    }

    // Sacar el email del usuario a partir del token (esto es necesareo para inyectarlo en el SecurityContextHolder)

    // Verificar la validez de un token
}
