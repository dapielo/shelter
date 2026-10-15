package com.david.shelter.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDTO(
    String email,
    String username,
    @NotBlank (message = "La contraseña no puede estar vacía")
    String password,
    String newPassword,
    String passwordVerification
) {

}
