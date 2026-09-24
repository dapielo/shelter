package com.david.shelter.dto;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

public record AdoptionDTO(
    String userEmail,
    Long petId,
    LocalDate initialDate,
    LocalDate endingDate,
    String status
) { }
