package com.david.shelter.dto;

public record LoginResponseDTO(
    Long id,
    String username,
    String email,
    String token
) {}
