package com.david.shelter.dto;

import java.time.LocalDate;
import com.david.shelter.model.enums.AgeGroup;
import com.david.shelter.model.enums.PetStatus;
import com.david.shelter.model.enums.Species;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Podemos dejar Enums en el DTO ya que la libreria Jackson hace el parseo de String al Enum correspondiente de forma automatica
public record PetDTO(
    Long id,
    @NotBlank (message = "El nombre de la mascota no puede estar vacío")
    String name,
    @NotNull (message = "Hay que indicar una especie")
    Species species,
    String breed,
    AgeGroup ageGroup,
    String info,
    String registeredAt,
    @NotNull (message = "tiene que tener una casa de acogida asociada")
    Long fosterHomeId,
    PetStatus status,
    String imagePath
) { }
