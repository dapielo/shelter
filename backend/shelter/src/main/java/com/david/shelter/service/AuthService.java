package com.david.shelter.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.david.shelter.dto.AuthenticationDTO;
import com.david.shelter.dto.LoginRequestDTO;
import com.david.shelter.dto.LoginResponseDTO;
import com.david.shelter.exception.LoginException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.model.entity.User;
import com.david.shelter.model.enums.Role;
import com.david.shelter.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
// Este servicio se encarga de recibir un intento de login, y con el crear un objeto authentication con el email
// y la contraseña del usuario, y pasarselo al JwtService, para que genere un token que este al recibir devuelve
// al controlador cuando lo llame
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO login) {

        // Buscamos el usuario, si el email es incorrecto lanzamos excepcion
        User user = userRepository.findByEmail(login.email()).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado un usuario con ese email en el intento de inicio de sesion"));

        // Comprobamos que la contraseña sea correcta
        if (!passwordEncoder.matches(login.password(), user.getPassword())) {
            throw new LoginException("La contraseña no es correcta");
        }
        
        // Sacamos el email que es lo que identifica el usuario y el rol
        String userEmail = user.getEmail();
        Role rol = user.getRole();

        // Creamos un objeto AuthenticationDTO, que es un record que he creado para pasar datos de autenticacion entre el AuthService y el JwtService
        AuthenticationDTO auth = new AuthenticationDTO(userEmail, rol);

        // le pasamos el objeto auth para que cree el token con el, y lo devolvemos 
        String token = jwtService.generateToken(auth);
        return new LoginResponseDTO(user.getId(), user.getUsername(), token);
    }
}
