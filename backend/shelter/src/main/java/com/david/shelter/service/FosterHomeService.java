package com.david.shelter.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.david.shelter.dto.FosterHomeDTO;
import com.david.shelter.exception.FosterHomeException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.mapper.FosterHomeMapper;
import com.david.shelter.model.entity.Adoption;
import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.entity.User;
import com.david.shelter.repository.AdoptionRepository;
import com.david.shelter.repository.FosterHomeRepository;
import com.david.shelter.repository.PetRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class FosterHomeService {

    private final FileService fileService;
    private final FosterHomeRepository fosterHomeRepository;
    private final UserService userService;
    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;

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
        // ponemos el id a null, por si venia algo en el, si nos lo envian un con un valor que existe en la BD se sobreescribiria esa casa de acogida con los datos de esta
        fosterHome.setId(null);
        // Le asociamos el usuario
        User user = userService.getUserFromContext();
        fosterHome.setUser(user);
        // Inicializamos la lista de mascotas, aunque de momento este vacía, luego para añadir una mascota solo tenemos que acceder a ella sin 
        // comprobar antes si esta está vacía
        fosterHome.setPets(new ArrayList<Pet>());

        try {
            FosterHome saved = fosterHomeRepository.save(fosterHome);
            return FosterHomeMapper.toDTO(saved);
        // capturamos los errores que puede lanzar el save
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new FosterHomeException("No se ha podido registrar la casa de acogida, algún dato era incorrecto");
        }
    }

    // READ
    public List<FosterHomeDTO> findByUserWithPets() {
        
        User user = userService.getUserFromContext();

        // Devolvemos la lista de coincidencias, mapeandolas antes como DTOs para enviarlas al front
        return fosterHomeRepository.findByUserWithPets(user)
            .stream()
            .map(FosterHomeMapper::toDTO)
            .toList();

    }

    // UPDATE
    // Forzamos a que se envien los datos obligatorios en la peticion, otra forma de hacerlo podria ser que si el dto trae uno de los campos
    // obligatorios vacío, se conserve el anterior, pero creo que es mejor indicar al front que tiene que enviar los datos obligatorios cubiertos siempre
    @Transactional 
    public FosterHomeDTO updateFosterHome(FosterHomeDTO dto){

        if (dto.id() == null) {
            throw new ResourceNotFoundException("Hay que indicar el id de la casa de acogida que se quiere actualizar");
        }

        // Buscamos el usuario
        User user = userService.getUserFromContext();

        // Buscamos la casa de acogida
        FosterHome fosterHome = fosterHomeRepository.findById(dto.id())
                                .orElseThrow(() -> new FosterHomeException("Hogar de acogida no encontrado al intentar cambiar sus datos. ID: " + dto.id()));

        // Comprobamos que realmente se esta intentadno actualizar una casa de acogida del propio usuario (un usuario con conocimientos de Postman podria
        // hacer una peticion de actualizacion de una casa de acogida que no le pertenece cambiando el id en el JSON de la peticion)
        if (fosterHome.getUser().getId().longValue() != user.getId().longValue()) {
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
            return FosterHomeMapper.toDTO(fosterHomeRepository.save(fosterHome));
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
        User user = userService.getUserFromContext();
        
        if (fosterHome.getUser().getId().longValue() != user.getId().longValue()) {
            throw new FosterHomeException("No se ha borrado la casa de acogida, no pertenece a este usuario");
        }

        // Buscamos las adopciones asociadas a mascotas de esta casa de acogida
        List<Adoption> adoptionsByOwner = adoptionRepository.findByOwner(user);
        List<Adoption> adoptionsInThisFoseterHome = adoptionsByOwner.stream()
                                                        .filter(adoption -> adoption.getPet().getFosterHome().getId().longValue() == fosterHome.getId().longValue())
                                                        .toList();
        
        // Buscamos las mascotas asociadas a esta casa de acogida para borrar sus imagenes del FileService
        List<Pet> petsInThisFosterHome = petRepository.findByFosterHome(fosterHome);

        // Ahora borramos las adopciones asociadas porque tienen clave foranea de las mascotas que hay en la casa de acogida
        adoptionRepository.deleteAll(adoptionsInThisFoseterHome);
        // Ahora borramos la casa de acogida (por si el borrado falla)
        fosterHomeRepository.delete(fosterHome);
        // Forzamos la actualizacion de la base de datos para ver si hay errores, sino perderiamos las imagenes sin que se borrase nada
        fosterHomeRepository.flush();
        // por ultimo borramos las imagenes de las mascotas que habia en ella
        petsInThisFosterHome.stream()
                            .forEach(pet -> {fileService.deleteImage(pet.getImagePath());});

    }

}
