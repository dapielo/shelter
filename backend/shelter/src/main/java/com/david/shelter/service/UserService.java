package com.david.shelter.service;

import com.david.shelter.repository.AdoptionRepository;
import java.util.Optional;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.david.shelter.dto.PasswrodChangeDTO;
import com.david.shelter.dto.UserDTO;
import com.david.shelter.exception.PasswordNotMatchingException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.exception.UserException;
import com.david.shelter.exception.UserFromTokenNotFoundEception;
import com.david.shelter.mapper.UserMapper;
import com.david.shelter.model.entity.User;
import com.david.shelter.model.enums.Role;
import com.david.shelter.repository.UserRepository;
import com.david.shelter.util.Validator;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UserService {
    
    private final AdoptionRepository adoptionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    // IMPLEMENTAMOS EL CRUD

    // CREATE
    @Transactional // Esta anotacion permite que este metodo se trate como una transaccion, si un paso falla, toda la operacion falla, se trata como una operacion atómica
    public UserDTO createUser(UserDTO dto) {

        // Si el nombre de usuario tiene menos de 3 caracteres, o mas de 10, o no empieza por una letra, no es valido
        if(dto.username().length() < 3 || dto.username().length() < 10 || !Character.isLetter(dto.username().charAt(0))) {
            throw new UserException("El nombre de usuario ha de empezar por al menos una letra y tener entre 3 y 10 caracteres");
        }
        // Comprobamos que el email tenga un formato correcto
        if (!Validator.isValidEmail(dto.email())) {
            throw new UserException("El email no tiene un formato correcto");
        }
        // Comprobamos que la contraseña tenga entre 8 y 20 caracteres
        if (dto.password().length() < 8 || dto.password().length() > 20) {
            throw new UserException("La contrasñea tiene que tener entre 8 y 20 carateres");
        }

        Optional<User> existent = userRepository.findByEmail(dto.email());

        // Comprobamos que no haya alguien registrado con ese mismo email
        if (existent.isPresent()) {
            throw new UserException("Ya existe un usuario con ese email");
        }

        // NOTA: No se pueden crear usuarios admin, el unico usuario admin se inyecta directamente en la base de datos
        String encodedPassword = encoder.encode(dto.password());
        User newUser = new User(null, dto.email(), dto.username(), encodedPassword, null,Role.ROLE_USER);

        // Metemos en un bloque try-catch para capturar el IllegalArgumentException y OptimisticLockingFailureException que puede lanzar el .save() y lanzar un error mas significativo
        try {
            UserDTO saved = UserMapper.toDTO(userRepository.save(newUser));
            return saved;
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new UserException("Algun dato del usuario no es correcto");
        }
    }

    // READ
    public UserDTO getByEmail(String email) {
        
        // JpaRepository devuelve Optionals, y estos tienen un metodo .map() que si el optional no esta vacio, permite pasarle un mapper para hacer la conversion
        // tambien nos permite parale una lambda para que se ejecute si el Optional viene vacio, con el metodo .orElseThrow()
        return userRepository.findByEmail(email)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con ese email"));
    }

    // Este método busca el usuario a partir del email almacenado en el SecurityContextHolder que usaremos en las peticiones que neesitan autenticacion
    public User getUserFromContext() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new UserFromTokenNotFoundEception("No se ha encontrado el usuario que hizo la consutla en la base de datos."));
    }

    // UPDATE
    // este metodo es para cambiar el nombre de usuario, no devuelve nada
    @Transactional 
    public void updateUsername(UserDTO dto){

        User user = userRepository.findByEmail(dto.email())
                        .orElseThrow(() -> new ResourceNotFoundException("No Hay ningun usuario registrado con ese email"));
        
        // Si no se lanzo una excepcion, el usuario existe
        user.setUsername(dto.username());
        
        try {
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new UserException("El nombre de usuario no se ha podido cambiar, no es válido");
        }
    }

    // Este método es para cambiar la contraseña, recibe un dto con la contraseña vieja, y la nueva escrita dos veces, el usuario lo obtenemos del 
    // SecurityContextHolder, y el metodo no devuelve nada
    @Transactional 
    public void updatePassword(PasswrodChangeDTO dto) {
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado el usuario en la peticion de cambio de contraseña"));

        // Comprobamos que la contraseña nueva y su verificacion coinciden
        if (!dto.newPassword().equals(dto.newPasswordAgain())){
            throw new PasswordNotMatchingException("Las contraseña nueva y replica no coinciden");
        }
        // Comrpobamos que le contraseña antigua es correcta
        if (!encoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new PasswordNotMatchingException("La contraseña antigua no es correcta");
        }

        String newPasswordEncripted = encoder.encode(dto.newPassword());
        user.setPassword(newPasswordEncripted);
        userRepository.save(user);
    }

    // DETELE 
    // Al no ser una aplicacion comercial, si un usuario desea eliminar sus usuario, se respeta su privacidad y se eliminan todos los datos asociados
    @Transactional 
    public void deleteUser(String userEmail) {
        
        // Si esta presente, lo borra, y si no esta presente, se lanza una excepcion
        userRepository.findByEmail(userEmail).ifPresentOrElse(
            // NOTA: si se borra un usuario, se borran en cascada sus casas de acogida, que a su vez borra en cascada sus mascotas
            // pero las adopciones no se borran en cascada al borrar al usuario, y por privacidad queremos que el borrado de una cuenta de usuario no deje rastro
            (user) -> 
                {
                    adoptionRepository.deleteAll(adoptionRepository.findByAdopter(user));
                    adoptionRepository.deleteAll(adoptionRepository.findByOwner(user));
                    userRepository.deleteByEmail(userEmail);
                }, 
            () -> new ResourceNotFoundException("No se ha borrado el usuario porque no existia un usuario con ese email"));
    }
}
