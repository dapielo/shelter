package com.david.shelter.dto;

import java.util.List;
import com.david.shelter.model.enums.Province;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FosterHomeDTO(
    // Lleva el id para que el front lo conozca para cuando se haga algun cambio, poder diferenciar de que hogar de acogida hablamos ya que 
    // un usuario puede tener varios
    Long id,
    @NotBlank 
    String name,
    @NotNull
    @Min (value = 1) 
    Integer capacity,
    @NotNull 
    Province province,
    List<PetDTO> pets   
) { }
