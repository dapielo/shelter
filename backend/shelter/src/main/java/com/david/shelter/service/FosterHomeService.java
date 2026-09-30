package com.david.shelter.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david.shelter.dto.FosterHomeDTO;
import com.david.shelter.exception.FosterHomeException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.mapper.FosterHomeMapper;
import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.entity.User;
import com.david.shelter.model.enums.Province;
import com.david.shelter.repository.FosterHomeRepository;
import com.david.shelter.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class FosterHomeService {

    private final FosterHomeRepository fosterHomeRepository;
    private final UserRepository userRepository;

    // IMPLEMENTAMOS EL CRUD

    // CREATE
    @Transactional 
    public FosterHomeDTO createFosterHome(FosterHomeDTO dto) {

        // Si el nombre viene vacio o tiene mas de 20 letras se cancela la operacion
        if (dto.name().length() < 1 || dto.name().length() > 20) {
            throw new FosterHomeException("El nombre de la casas de acogida tiene que tener entre 1 y 20 caracteres");
        }

        // Si la capacidad es menor que 1 se cancela la operacion
        if (dto.capacity() < 1) {
            throw new FosterHomeException("La capacidad de la casa de acogida no puede ser menor que 1");
        }

        FosterHome fosterHome = FosterHomeMapper.toEntity(dto);
        // Le asociamos el usuario
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new ResourceNotFoundException("El usuario no se ha encontrado en la creacion de un hogar de acogida"));
        fosterHome.setUser(user);
        // Inicializamos la lista de mascotas, aunque de momento este vacía, luego para añadir una mascota solo tenemos que acceder a ella sin 
        // comprobar antes si esta está vacía
        fosterHome.setPets(new ArrayList<Pet>());

        try {
            FosterHome saved = fosterHomeRepository.save(fosterHome);
            return FosterHomeMapper.toDTO(saved);
        } catch (IllegalArgumentException e) {
            throw new FosterHomeException("No se ha podido registrar la casa de acogida, algún dato era incorrecto");
        }
    }

    // READ
    // El usuario lo sacamos del SecurityContextHolder, tnemos un metodo en el repositorio que busca todas las filas de la tabla foster_homes
    // que tengan asociado un Usuario en concreto (el SGBD lo busca por el id del usuario)
    public List<FosterHomeDTO> findByUserWithPets() {
        
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                        .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario resgitrado con ese email, al cargar el listado de casas de acogida"));
        
        // Devolvemos la lista de coincidencias, mapeandolas antes como DTOs para enviarlas al front
        return fosterHomeRepository.findByUserWithPets(user)
            .stream()
            .map(FosterHomeMapper::toDTO)
            .toList();

    }

    // UPDATE
    @Transactional 
    public void updateFosterHome(FosterHomeDTO dto){
        // Buscamos el usuario
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado al intentar actualizar la informacion de un hogar de acogida"));

        // Buscamos la casa de acogida
        FosterHome fosterHome = fosterHomeRepository.findById(dto.id())
                                .orElseThrow(() -> new ResourceNotFoundException("Hogar de acogida no encontrado al intentar cambiar sus datos"));

        // Comprobamos que realmente se esta intentadno actualizar una casa de acogida del propio usuario (un usuario con conocimientos de Postman podria
        // hacer una peticion de actualizacion de una casa de acogida que no le pertenece cambiando el id en el JSON de la peticion)
        if (fosterHome.getUser().getId() != user.getId()) {
            throw new FosterHomeException("Se han intentado actualizar datos de un hogar de acogida que no le pertenece");
        }

        // Si el nombre viene vacio o tiene mas de 20 letras se cancela la operacion
        if (dto.name().length() < 1 || dto.name().length() > 20) {
            throw new FosterHomeException("El nombre de la casas de acogida tiene que tener entre 1 y 20 caracteres");
        }

        // Si la capacidad es menor que 1 se cancela la operacion
        if (dto.capacity() < 1) {
            throw new FosterHomeException("La capacidad de la casa de acogida no puede ser menor que 1");
        }
        
        fosterHome.setName(dto.name());
        fosterHome.setCapacity(dto.capacity());
        fosterHome.setProvince(dto.province());
  
        try {
            fosterHomeRepository.save(fosterHome);
        } catch (IllegalArgumentException e) {
            throw new FosterHomeException("Algún dato de la casa de acogida no es válido, no se ha podido cambiar");
        }
    }

    // DELETE
    @Transactional 
    public void deleteFosterHome(Long id) {
        // Buscamos la casa de acogida
        FosterHome fosterHome = fosterHomeRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la casa de acogida al intentar borrarla"));
        // Buscamos el usuario
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado al intentar borrar la casa de acogida"));
        
        
        if (fosterHome.getUser().getId() != user.getId()) {
            throw new FosterHomeException("No se ha borrado la casa de acogida, no pertenece a este usuario");
        }

        fosterHomeRepository.delete(fosterHome);
    }

}
