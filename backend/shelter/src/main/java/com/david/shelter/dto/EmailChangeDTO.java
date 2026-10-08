package com.david.shelter.dto;

import jakarta.validation.constraints.NotNull;

public record EmailChangeDTO(
    @NotNull 
    String email,
    @NotNull 
    String password
) { }
