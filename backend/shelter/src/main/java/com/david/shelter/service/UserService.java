package com.david.shelter.service;

import com.david.shelter.repository.AdoptionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.david.shelter.dto.AuthenticationDTO;
import com.david.shelter.dto.EmailChangeDTO;
import com.david.shelter.dto.LoginResponseDTO;
import com.david.shelter.dto.PasswordChangeDTO;
import com.david.shelter.dto.UserDTO;
import com.david.shelter.exception.InvalidEmailException;
import com.david.shelter.exception.PasswordNotMatchingException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.exception.UserException;
import com.david.shelter.exception.UserFromTokenNotFoundEception;
import com.david.shelter.exception.UserNotAuthorizedException;
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
    private final JwtService jwtService;
    private final FileService fileService;

    // IMPLEMENTAMOS EL CRUD

    // CREATE
    @Transactional // Esta anotacion permite que este metodo se trate como una transaccion, si un paso falla, toda la operacion falla, se trata como una operacion atómica
    public UserDTO createUser(UserDTO dto) {

        // Si el nombre de usuario tiene menos de 3 caracteres, o mas de 10, o no empieza por una letra, no es valido
        if(dto.username().length() < 3 || dto.username().length() > 10 || !Character.isLetter(dto.username().charAt(0))) {
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

    // Obtenemos una lista de usuarios, esto solo lo va a poder usar el administrador
    public List<UserDTO> getUsers() {

        return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
    }

    // UPDATE
    // este metodo es para cambiar el nombre de usuario, no devuelve nada
    @Transactional 
    public void updateUsername(UserDTO dto){

        User user = getUserFromContext();

        // Si el nombre de usuario tiene menos de 3 caracteres, o mas de 10, o no empieza por una letra, no es valido
        if(dto.username().length() < 3 || dto.username().length() > 10 || !Character.isLetter(dto.username().charAt(0))) {
            throw new UserException("El nombre de usuario ha de empezar por al menos una letra y tener entre 3 y 10 caracteres");
        }

        if (!dto.email().equals(user.getEmail())){
            throw new UserException("ese email no pertenece a este usuario");
        }

        if (!encoder.matches(dto.password(), user.getPassword())){
            throw new UserException("La contraseña del usuario no es correcta");
        }
        
        // Si no se lanzo una excepcion, el usuario existe y sus datos son correctos
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
    public void updatePassword(PasswordChangeDTO dto) {
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado el usuario en la peticion de cambio de contraseña"));

        if (dto.newPassword().length() < 8 || dto.newPassword().length() > 20) {
            throw new UserException("La contraseña tiene que tener entre 8 y 20 caracteres");
        }

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

    @Transactional 
    // Devuelve un LoginResponse, poruqe al cambiar el email generamos un nuevo token
    public LoginResponseDTO updateEmail(EmailChangeDTO dto) {

        User user = getUserFromContext();

        String newEmail = dto.email();

        // Comrpobamos que el email tenga un formato valido
        if (!Validator.isValidEmail(newEmail)) {
            throw new UserException("El formato del email no es valido");
        }
        
        // Comprobamos que la contraseña este bien, nos tiene que enviar contraseña para cambiar el email
        if (!encoder.matches(dto.password(), user.getPassword())) {
            throw new UserException("La contraseña no es correcta");
        }

        // Comprobamos que el email no este ya en la base de datos, porque tiene que ser único.
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new InvalidEmailException("Ya existe un usuario con ese email");
        }

        // Si todo esta bien, procedemos a cambiar el email, generamos un nuevo token (el anterior dejara de ser valido) y lo devolvemos
        user.setEmail(newEmail);
        try {
            userRepository.saveAndFlush(user);
            String token = jwtService.generateToken(new AuthenticationDTO(newEmail, user.getRole()));
            return new LoginResponseDTO(user.getId(), token);
        } catch (RuntimeException e) {
            throw new UserException("No se ha podido cambiar el email del Usuario");
        }
    }

    // DETELE 
    // Al no ser una aplicacion comercial, si un usuario desea eliminar sus usuario, se respeta su privacidad y se eliminan todos los datos asociados
    @Transactional 
    public void deleteUser(String userEmail) {

        if (!Validator.isValidEmail(userEmail)) {
            throw new InvalidEmailException("El email no es valido");
        } 
        User user = getUserFromContext();

        // Si el usuario no es administrador y su email no es el email del usuario que se pretende borrar, lanzamos excepcion
        if (user.getRole()!=Role.ROLE_ADMIN && !user.getEmail().equals(userEmail)) {
            throw new UserNotAuthorizedException("No puedes borrrar a ese usuario");
        }

        User userToDelete = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("No hay un usuario con ese email"));

        // No se puede borrar la cuenta de administrador
        if (userToDelete.getRole() == Role.ROLE_ADMIN) {
            throw new UserNotAuthorizedException("No se puede eliminar este usuario");
        }

        // Borramos la informacion de adopciones asociada al usuario
        adoptionRepository.deleteAll(adoptionRepository.findByAdopter(userToDelete));
        adoptionRepository.deleteAll(adoptionRepository.findByOwner(userToDelete));

        // Borramos el usuario
        userRepository.delete(userToDelete);
        // Hacemos el delete en la BD para ver que no haya errores antes de borrar su carpeta de usuario
        userRepository.flush();
        // ahora borramos la carpeta de imagenes que tenia asociada
        fileService.deleteUserFolder(userToDelete.getId());
        
    }
}
