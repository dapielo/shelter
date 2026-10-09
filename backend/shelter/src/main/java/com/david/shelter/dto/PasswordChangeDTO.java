package com.david.shelter.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeDTO (
    @NotBlank (message = "la contraseña antigua no puede venir vacía")
    String oldPassword,
    @NotBlank (message = "La contraseña nueva no puede venir vacía")
    String newPassword,
    @NotBlank (message = "La contraseña nueva tiene que ser intriducida dos veces")
    String newPasswordAgain
) {}
