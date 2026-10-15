package com.david.shelter.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.david.shelter.dto.LoginResponseDTO;
import com.david.shelter.dto.UpdateUserDTO;
import com.david.shelter.dto.UserDTO;
import com.david.shelter.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping ("/shelter/user")
// Este servicio no crea usuarios, del login y el registro se encarga el AuthController, este servicio tiene dos objetivos, que los usuarios puedan
// cambiar sus datos o borrar su cuenta, y que el administrador pueda eliminar usuarios que hagan publicaciones que no cumplen con el objetivo de Shelter
public class UserController {

    private final UserService userService;

    // READ
    // listado de todos los usuatrios, solo para el admin
    @PreAuthorize ("hasRole('ADMIN')")
    @GetMapping 
    public ResponseEntity<List<UserDTO>> getUsers() {

        List<UserDTO> body = userService.getUsers();
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    // UPDATE
    @PreAuthorize ("isAuthenticated()")
    @PutMapping ("/update")
    public ResponseEntity<LoginResponseDTO> updateUser(@Valid @RequestBody UpdateUserDTO dto) {

        LoginResponseDTO body = userService.updateUser(dto);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }

    /* 
    // Cambiar el nombre de usuario, recibe un campo "username" con el nuevo nombre de usuario
    @PreAuthorize ("isAuthenticated()")
    @PutMapping ("/edit/{username}")
    public ResponseEntity<Void> changeUsername(@PathVariable String username) {

        userService.updateUsername(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Cambiar la contraseña
    @PreAuthorize ("isAuthenticated()")
    @PutMapping ("edit/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeDTO dto) {

        userService.updatePassword(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Cambiar el email
    @PreAuthorize ("isAuthenticated()")
    @PutMapping ("/edit/email")
    public ResponseEntity<LoginResponseDTO> changeEmail(@Valid @RequestBody EmailChangeDTO dto) {

        LoginResponseDTO body = userService.updateEmail(dto);
        return new ResponseEntity<>(body,HttpStatus.OK);
    }
*/
    // DELETE
    @PreAuthorize ("isAuthenticated()") 
    @DeleteMapping ("/delete/{userEmail}")
    public ResponseEntity<Void> deleteuser(@PathVariable String userEmail) {

        userService.deleteUser(userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
