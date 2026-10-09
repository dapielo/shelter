package com.david.shelter.dto;

import jakarta.validation.constraints.NotNull;

public record EmailChangeDTO(
    @NotNull (message = "El email no puede estar vacío")
    String email,
    @NotNull (message = "La contraseña no puede estar vacía")
    String password
) { }
