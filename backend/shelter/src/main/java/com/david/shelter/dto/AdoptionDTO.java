package com.david.shelter.dto;

import java.time.LocalDate;

import com.david.shelter.model.enums.Status;

public record AdoptionDTO(
    Long id,
    Long petId,
    String petName,
    LocalDate initialDate,
    LocalDate endingDate,
    Status status,
    // Este campo no existe en la Entidad, se va usar para las solicitudes aceptadas de adopcion, y se va a rellenar con la informacion de 
    // un usuario u otro al momento de hacer la peticion, en caso de que la adopcion tenga marcada como status ACCEPTED
    String ownerEmail,
    String adopterEmail
) { }
