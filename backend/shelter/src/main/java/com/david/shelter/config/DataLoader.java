package com.david.shelter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.david.shelter.model.entity.User;
import com.david.shelter.model.enums.Role;
import com.david.shelter.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component  
@RequiredArgsConstructor 
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    // Inyectamos el email y contraseña del admin
    @Value ("${admin.email}")
    private String adminEmail;
    @Value ("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {

        // Creamos el administrador en la base de datos
        if (!userRepository.findByEmail(adminEmail).isPresent()) {    
            
            User admin = new User(null, adminEmail, "admin", encoder.encode(adminPassword), null, Role.ROLE_ADMIN);
            userRepository.save(admin);
        }
    }
}
