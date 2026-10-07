package com.david.shelter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.david.shelter.dto.LoginRequestDTO;
import com.david.shelter.dto.LoginResponseDTO;
import com.david.shelter.dto.UserDTO;
import com.david.shelter.service.AuthService;
import com.david.shelter.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

// Este controlador se encarga únicamente del login, y del registro de usuario y obivamente no requiere de autenticacion de ningun tipo
@RestController // Utiliza la libreria Jackson para parsear JSON a Objeto de Java 
@RequiredArgsConstructor 
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/shelter/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO login) {
        LoginResponseDTO body = authService.login(login);
        return new ResponseEntity<>(body,HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/shelter/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody UserDTO dto) {
        // Primero creamos el usuario
        UserDTO newUser = userService.createUser(dto);
        LoginRequestDTO login = new LoginRequestDTO(newUser.email(), newUser.password());

        // Ahora lo autenticamos y le devolvemos el token, para que no tenga que meter usuraio y contraseña justo despues de crear el usuario
        LoginResponseDTO body = authService.login(login);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }
}
