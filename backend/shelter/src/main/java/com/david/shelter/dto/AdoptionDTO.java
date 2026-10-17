package com.david.shelter.dto;

import java.time.LocalDate;
import com.david.shelter.model.enums.AdoptionStatus;

// No hago comprobaciones porue esta entidad nunca me viene de fuera, la uso uso desde dentro para enviar datos al front
public record AdoptionDTO(
    Long id,
    Long petId,
    String petName,
    LocalDate initialDate,
    LocalDate endingDate,
    AdoptionStatus status,
    // Estos campos no existe en la Entidad, se va usar para las solicitudes aceptadas de adopcion, y se va a rellenar con la informacion de 
    // un usuario u otro al momento de hacer la peticion, en caso de que la adopcion tenga marcada como status ACCEPTED
    String ownerEmail,
    String adopterEmail
) { }
