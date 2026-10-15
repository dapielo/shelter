package com.david.shelter.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.david.shelter.dto.PetDTO;
import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.enums.PetStatus;

public class PetMapper {

    // Para que el mapper no tenga acceso al servicio, es este al llamarlo el que tiene que haber localizado antes el hogar de acogida
    public static Pet toEntity(PetDTO dto, FosterHome fosterHome) {

        return new Pet(
            null, 
            dto.name(), 
            dto.species(), 
            dto.breed(), 
            dto.ageGroup(), 
            dto.info(), 
            LocalDate.now(), 
            fosterHome,
            PetStatus.AVAILABLE,
            null
        );
    }

    public static PetDTO toDTO(Pet pet) {

        return new PetDTO(
            pet.getId(), 
            pet.getName(), 
            pet.getSpecies(), 
            pet.getBreed(), 
            pet.getAgeGroup(), 
            pet.getInfo(), 
            pet.getRegisteredAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
            pet.getFosterHome().getId(),
            pet.getStatus(),
            pet.getImagePath()
        );
    }
}
