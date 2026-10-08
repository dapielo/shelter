package com.david.shelter.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.david.shelter.dto.FosterHomeDTO;
import com.david.shelter.service.FosterHomeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Controlador para gestionar las casas de acogida, como el usuario va en el token, no neesitamos recibir parámetro sobre el mismo
// ya se encarga de buscarlo el servicio
@RestController  // Utiliza la libreria Jackson para parsear JSON a Objeto de Java
@RequiredArgsConstructor 
@RequestMapping("/shelter/fosterhome") // Esto indica la ruta raiz para todos los metodos del controlador
public class FosterHomeController {

    private final FosterHomeService fosterHomeService;
    
    // CREATE
    // Crear una nueva casa de acogida
    @PostMapping("/create")
    @PreAuthorize ("isAuthenticated()")
    public ResponseEntity<FosterHomeDTO> createFosterHome(@Valid @RequestBody FosterHomeDTO dto) {

        FosterHomeDTO body = fosterHomeService.createFosterHome(dto);
        return new ResponseEntity<>(body,HttpStatus.CREATED);
    }

    // READ
    // Obtener el listado de hogares de acogida con sus respectivas mascotas del usuario
    @GetMapping
    @PreAuthorize ("isAuthenticated()")
    public ResponseEntity<List<FosterHomeDTO>> getFosterHomesWithPets() {
        
        List<FosterHomeDTO> body = fosterHomeService.findByUserWithPets();
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // UPDATE
    // Modificar los datos de una casa de acogida
    @PutMapping ("/edit")
    @PreAuthorize ("isAuthenticated()")
    public ResponseEntity<FosterHomeDTO> editFosterHome(@Valid @RequestBody FosterHomeDTO dto) {

        FosterHomeDTO body = fosterHomeService.updateFosterHome(dto);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // DELETE
    // Borrar una casa de acogida de la base de datos, nos pasan el id de la casa de acogida en el query string
    @DeleteMapping ("/delete/{id}")
    @PreAuthorize ("isAuthenticated()")
    // Para las respuestas vacias de contenido usamos la clase envoltorio Void, que envuelve la representacion de la palabra reservada void
    public ResponseEntity<Void> deleteFosterHome(@PathVariable  Long id) {

        fosterHomeService.deleteFosterHome(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
