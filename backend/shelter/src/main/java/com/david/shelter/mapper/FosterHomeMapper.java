package com.david.shelter.mapper;

import com.david.shelter.dto.FosterHomeDTO;
import com.david.shelter.model.entity.FosterHome;

public class FosterHomeMapper {

    public static FosterHome toEntity(FosterHomeDTO dto) {

        // El usuario se va poder consultar en el SecurityContextHolder, y los pets son los 
        // dueños de la relacion asi que no necesito registarlos en la Entidad para guardarla/modificarla
        return new FosterHome(
            dto.id(), 
            dto.capacity(), 
            dto.province(), 
            null,
            null
        );
    }

    public static FosterHomeDTO toDTO(FosterHome fosterHome) {

        return new FosterHomeDTO(
            fosterHome.getId(), 
            fosterHome.getCapacity(), 
            fosterHome.getProvince()
        );
    }
}
