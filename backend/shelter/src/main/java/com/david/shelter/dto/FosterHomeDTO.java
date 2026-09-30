package com.david.shelter.dto;

import java.util.List;

import com.david.shelter.model.enums.Province;

public record FosterHomeDTO(
    // Lleva el id para que el front lo conozca para cuando se haga algun cambio, poder diferenciar de que hogar de acogida hablamos ya que 
    // un usuario puede tener varios
    Long id,
    String name,
    Integer capacity,
    Province province,
    List<PetDTO> pets   
) { }
