package com.david.shelter.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.david.shelter.dto.AdoptionDTO;
import com.david.shelter.service.AdoptionService;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping ("/shelter/adoption")
public class AdoptionController {

    private final AdoptionService adoptionService;

    // CREATE 
    @PreAuthorize ("isAuthenticated()")
    @PutMapping ("/initiate/{petId}")
    public ResponseEntity<AdoptionDTO> initiateAdoption(@PathVariable Long petId) {

        AdoptionDTO body = adoptionService.initiateAdoption(petId);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // READ
    // Lsita de adopciones en las que el usuario es el que quiere adoptar
    @PreAuthorize ("isAuthenticated()")
    @GetMapping ("/asAdopter") 
    public ResponseEntity<List<AdoptionDTO>> getAdoptionsAsAdopter() {

        List<AdoptionDTO> body = adoptionService.getAdoptionsByAdopter();
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // Lista de adopciones en las que el usuario es el que tiene al animal de acogida
    @PreAuthorize ("isAuthenticated()")
    @GetMapping ("/asOwner")
    public ResponseEntity<List<AdoptionDTO>> getAdoptionsAsOwner() {

        List<AdoptionDTO> body = adoptionService.getAdoptionsByOwner();
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // Lista de adopciones cerradas
    @PreAuthorize ("isAuthenticated()")
    @GetMapping ("/closed")
    public ResponseEntity<List<AdoptionDTO>> getAdoptionsHistory() {

        List<AdoptionDTO> body = adoptionService.getAdoptionsHistory();
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // UPDATE
    // aceptar una solicitud de adopcion 
    @PreAuthorize ("isAuthenticated()")
    @PutMapping ("/accept/{adoptionId}")
    public ResponseEntity<String> acceptAdoptionPetition(@PathVariable Long adoptionId) {

        String body = adoptionService.acceptAdoptionPetition(adoptionId);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // rechazar una solicitud de adopcion
    @PreAuthorize ("isAuthenticated()")
    @PutMapping ("/decline/{adoptionId}")
    public ResponseEntity<Void> declineAtoptionPetition(@PathVariable Long adoptionId) {

        adoptionService.declineAdoptionPetition(adoptionId);
        return ResponseEntity.ok().build();
    }

    // DELETE
    // Esto solo se usa para borrar una adopcion que se ha iniciado pero aun no ha sido aceptada o rechazada por la otra parte (sobretodo para cuando se hace click por error)
    @PreAuthorize ("isAuthenticated()")
    @DeleteMapping ("/delete/{petId}")
    public ResponseEntity<Void> deleteAdoption(@PathVariable Long petId) {

        adoptionService.deleteAdoptionPetition(petId);
        return ResponseEntity.ok().build();
    }
}
