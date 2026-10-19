package com.david.shelter.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.david.shelter.service.FileService;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor  
public class ImageController {

    private final FileService fileService;

    @GetMapping ("/shelter/image/{userId}/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable Long userId, @PathVariable String filename) {

        String imageRoute = userId + "/" + filename;

        // la imagen es el cuerpo de la respuesta
        Resource image = fileService.getImage(imageRoute);
        // al ser una imagen, vamos a meter unas cabeceras personalizadas para el Content-Type por si Spring no hace bien la inferencia de las cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaTypeFactory.getMediaType(image).orElse(MediaType.APPLICATION_OCTET_STREAM));

        // Usamos un contructor que admite tambien cabeceras, la tenemos que añadir al crear la ResponseEntity, luego las cabeceras son inmutables
        return new ResponseEntity<Resource>(image, headers, HttpStatus.OK);
    }
}
