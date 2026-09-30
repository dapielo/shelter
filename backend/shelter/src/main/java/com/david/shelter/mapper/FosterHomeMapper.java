package com.david.shelter.mapper;

import java.util.List;

import com.david.shelter.dto.FosterHomeDTO;
import com.david.shelter.dto.PetDTO;
import com.david.shelter.model.entity.FosterHome;

public class FosterHomeMapper {

    public static FosterHome toEntity(FosterHomeDTO dto) {

        // El usuario se va poder consultar en el SecurityContextHolder, y los pets son los 
        // dueños de la relacion asi que no necesito registarlos en la Entidad para guardarla/modificarla
        return new FosterHome(
            dto.id(), 
            dto.name(),
            dto.capacity(), 
            dto.province(), 
            null,
            null
        );
    }

    public static FosterHomeDTO toDTO(FosterHome fosterHome) {

        // Tenemos que hacer un mapeo previo del List de mascotas, para convertirlo en una lista de dtos, para luego añadirselo al FosterHome mapeado
        List<PetDTO> petsDTO = fosterHome.getPets()
            .stream()
            .map(PetMapper::toDTO)
            .toList();
            
        return new FosterHomeDTO(
            fosterHome.getId(), 
            fosterHome.getName(),
            fosterHome.getCapacity(), 
            fosterHome.getProvince(),
            petsDTO
        );
    }
}
