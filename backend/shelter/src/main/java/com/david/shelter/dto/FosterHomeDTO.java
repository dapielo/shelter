package com.david.shelter.dto;

import com.david.shelter.model.enums.Province;

public record FosterHomeDTO(
    Long id,
    Integer capacity,
    Province province    
) { }
