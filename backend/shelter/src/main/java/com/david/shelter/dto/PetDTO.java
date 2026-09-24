package com.david.shelter.dto;

import java.time.LocalDate;

// Los enums los vamos a aplanar a String para simplificar la serializacion en las respuestas
public record PetDTO(
    Long id,
    String name,
    String species,
    String breed,
    String ageGroup,
    String info,
    LocalDate registeredAt,
    Long fosterHomeId
) { }
