package com.david.shelter.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.david.shelter.dto.PetDTO;
import com.david.shelter.model.enums.Province;
import com.david.shelter.service.PetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/shelter/pet")
@RequiredArgsConstructor 
public class PetController {

    private final PetService petService;

    // CREATE
    @PostMapping ("/create")
    @PreAuthorize ("isAuthenticated()")
    // Quiero que el front me envie un FormData con un campo por cada atributo, y en la misma peticion, puede añadir un campo con la imagen, eso es lo que
    // caputra el @RequestPart, el campo del FromData en el que viene la imagen
    public ResponseEntity<PetDTO> createPet(@Valid @ModelAttribute PetDTO dto, @RequestPart (name = "image", required = false) MultipartFile image) {

        PetDTO body = petService.createPet(dto, image);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // READ
    // Para obtener una lista por provincia
    @GetMapping ("province/{province}")
    // El @PathVariable captura la variable de la URL que metimos arriba entre corchetes
    public ResponseEntity<List<PetDTO>> getPetsByProvince(@PathVariable Province province) {

        List<PetDTO> body = petService.findByProvince(province);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // Para obtener informacion de uno por id
    @GetMapping ("/id/{id}")
    public ResponseEntity<PetDTO> getPetById(@PathVariable  Long id) {

        PetDTO body = petService.getPetById(id);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // UPDATE
    @PutMapping ("/image/delete/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {

        petService.deleteImage(id);
        return ResponseEntity.ok().build(); // Esta es una forma alternativa de generar ResponseEntity con codigo Http OK (200)
    }

    @PutMapping ("update")
    public ResponseEntity<PetDTO> update(@ModelAttribute PetDTO dto, @RequestPart("image") MultipartFile image) {

        PetDTO body = petService.updatePet(dto, image);
        return ResponseEntity.ok(body);
    }

    // DELETE
    implementar
}
