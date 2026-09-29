package com.david.shelter.dto;

import java.util.List;

import com.david.shelter.model.enums.Province;

public record FosterHomeDTO(
    Long id,
    Integer capacity,
    Province province,
    List<PetDTO> pets   
) { }
