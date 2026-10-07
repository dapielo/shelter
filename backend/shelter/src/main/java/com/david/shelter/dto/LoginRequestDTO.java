package com.david.shelter.dto;

// DTO para el login a la hora de generar el token
public record LoginRequestDTO(
    String email,
    String password
) { }
