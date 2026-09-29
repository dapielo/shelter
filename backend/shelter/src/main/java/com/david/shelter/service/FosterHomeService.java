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
    public List<FosterHomeDTO> findByUser() {
        
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                        .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario resgitrado con ese email, al cargar el listado de casas de acogida"));
        
        ArrayList<FosterHomeDTO> fosterHomes = new ArrayList<>();

        // Añadimos al ArrayList la lista de coincidencias, mapeandolas antes como DTOs para enviarlas al front
        fosterHomes.addAll(fosterHomeRepository.findByUser(user)
            .stream()
            .map(FosterHomeMapper::toDTO)
            .toList()
        );
    }

    // UPDATE

    // DELETE

}
