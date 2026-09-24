package com.david.shelter.dto;

import java.time.LocalDate;
import com.david.shelter.model.enums.AgeGroup;
import com.david.shelter.model.enums.Species;

public record PetDTO(
    Long id,
    String name,
    Species species,
    String breed,
    AgeGroup ageGroup,
    String info,
    LocalDate registeredAt,
    Long fosterHomeId
) { }
