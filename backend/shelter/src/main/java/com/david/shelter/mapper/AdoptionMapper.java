package com.david.shelter.mapper;

import com.david.shelter.dto.AdoptionDTO;
import com.david.shelter.model.entity.Adoption;

// Este mapper solo tiene conversion a DTO, porque cuando se inicia o modifica una adopcion, el usuario lo puedo extraer del SecurityContextHolder,
// las fechas las establece el servicio y el estado tambien, como solo necesito el ID de la mascota, la voy a pedir al front como una variable de ruta
// en el endpoint correspondiente
public class AdoptionMapper {

    public static AdoptionDTO toDTO(Adoption adoption) {

        return new AdoptionDTO(
            adoption.getId(), 
            adoption.getPet().getId(), 
            adoption.getPet().getName(),
            adoption.getInitialDate(), 
            adoption.getEndingDate(),
            adoption.getStatus()
        );
    }
}
