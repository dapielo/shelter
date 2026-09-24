package com.david.shelter.dto;

// Aplanamos el enum Province a String para facilitar la serializacion
public record FosterHomeDTO(
    Long id,
    Integer capacity,
    String province    
) { }
