package com.david.shelter.dto;

import com.david.shelter.model.enums.Role;

// Objeto de autenticacion que se utiliza para pasar datos desde el AuthService hacia el JwtService
public record AuthenticationDTO(
    String user,
    Role rol
) { }
