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
    @NotBlank (message = "El nombre de la casa de acogida no puede estar vacio.")
    String name,
    @NotNull (message = "Hay que poner la capacidad de la casa de acogida")
    @Min (value = 1, message = "No puede tener capacidad menor que 1") 
    Integer capacity,
    @NotNull (message = "Hay que indicar una provincia")
    Province province,
    List<PetDTO> pets   
) { }
