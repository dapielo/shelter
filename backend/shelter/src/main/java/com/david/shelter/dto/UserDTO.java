package com.david.shelter.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
    @NotBlank 
    String email,
    @NotBlank 
    String username,
    String password
) {

}
