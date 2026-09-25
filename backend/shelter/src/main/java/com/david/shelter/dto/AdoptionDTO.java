package com.david.shelter.dto;

import java.time.LocalDate;

import com.david.shelter.model.enums.Status;

public record AdoptionDTO(
    Long id,
    Long petId,
    String petName,
    LocalDate initialDate,
    LocalDate endingDate,
    Status status
) { }
