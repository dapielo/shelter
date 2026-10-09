package com.david.shelter.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
    @NotBlank (message = "El email no puede estar vacío")
    String email,
    @NotBlank (message = "El nombre de usuario no puede estar vacío")
    String username,
    @NotBlank (message = "La contraseña no puede estar vacía")
    String password
) {

}
