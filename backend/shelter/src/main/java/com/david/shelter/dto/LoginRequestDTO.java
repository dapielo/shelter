package com.david.shelter.dto;

import jakarta.validation.constraints.NotBlank;

// DTO para el login a la hora de generar el token
public record LoginRequestDTO(
    @NotBlank (message = "El email no puede estar vacío")
    String email,
    @NotBlank (message = "La contraseña no puede estar vacía")
    String password
) { }
