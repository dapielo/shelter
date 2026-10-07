package com.david.shelter.dto;

import java.time.LocalDate;
import com.david.shelter.model.enums.AgeGroup;
import com.david.shelter.model.enums.Species;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Podemos dejar Enums en el DTO ya que la libreria Jackson hace el parseo de String al Enum correspondiente de forma automatica
public record PetDTO(
    Long id,
    @NotBlank 
    String name,
    @NotNull 
    Species species,
    String breed,
    AgeGroup ageGroup,
    String info,
    LocalDate registeredAt,
    @NotNull 
    Long fosterHomeId,
    String imagePath
) { }
