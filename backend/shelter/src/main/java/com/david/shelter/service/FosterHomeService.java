package com.david.shelter.service;

import com.david.shelter.controller.FosterHomeController;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.david.shelter.client.MapClient;
import com.david.shelter.client.dto.MapInfo;
import com.david.shelter.dto.FosterHomeDTO;
import com.david.shelter.exception.FosterHomeException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.mapper.FosterHomeMapper;
import com.david.shelter.model.entity.Adoption;
import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.entity.User;
import com.david.shelter.model.enums.PetStatus;
import com.david.shelter.repository.AdoptionRepository;
import com.david.shelter.repository.FosterHomeRepository;
import com.david.shelter.repository.PetRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class FosterHomeService {

    private final FileService fileService;
    private final MapClient mapClient;
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

        FosterHome fosterHome = FosterHomeMapper.toEntity(dto);
        fosterHome.setActive(true);
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
            MapInfo map = mapClient.getGeolocation(saved.getProvince()).orElse(null);
            return FosterHomeMapper.toDTO(saved, map != null ? map.boundingbox() : null, map != null ? map.display_name() : null);
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
            .map(fh -> {
                MapInfo map = mapClient.getGeolocation(fh.getProvince()).orElse(null);
                return FosterHomeMapper.toDTO(fh, map != null ? map.boundingbox() : null, map != null ? map.display_name() : null);
            })
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

        if (!fosterHome.isActive()) {
            throw new FosterHomeException("Esa casa de acogida ha sido borrada");
        }

        // Comprobamos que realmente se esta intentadno actualizar una casa de acogida del propio usuario (un usuario con conocimientos de Postman podria
        // hacer una peticion de actualizacion de una casa de acogida que no le pertenece cambiando el id en el JSON de la peticion)
        if (fosterHome.getUser().getId().longValue() != user.getId().longValue()) {
            throw new FosterHomeException("Se han intentado actualizar datos de un hogar de acogida que no le pertenece");
        }

        // Si el nombre viene vacio o tiene mas de 20 letras se cancela la operacion
        if (dto.name().length() < 1 || dto.name().length() > 20) {
            throw new FosterHomeException("El nombre de la casas de acogida tiene que tener entre 1 y 20 caracteres");
        }
        
        fosterHome.setName(dto.name());
        fosterHome.setProvince(dto.province());
  
        try {
            FosterHome saved = fosterHomeRepository.save(fosterHome);
            MapInfo map = mapClient.getGeolocation(saved.getProvince()).orElse(null);
            return FosterHomeMapper.toDTO(saved, map != null ? map.boundingbox() : null, map != null ? map.display_name() : null);
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
        List<Adoption> activeAdoptions = adoptionsByOwner.stream()
                                                        .filter(adoption -> {
                                                            boolean isInThisFH = adoption.getPet().getFosterHome().getId().longValue() == fosterHome.getId().longValue();
                                                            boolean isAvailable = adoption.getPet().getStatus() == PetStatus.AVAILABLE;
                                                            return isInThisFH && isAvailable;
                                                        })
                                                        .toList();
        
        // Buscamos las mascotas asociadas a esta casa de acogida para borrar sus imagenes del FileService
        List<Pet> availablePets = petRepository.findByFosterHome(fosterHome).stream()
                                                        .filter(p -> p.getStatus() == PetStatus.AVAILABLE)
                                                        .toList();

        // Borramos mascotas disponibles y adopciones de las que estaban disponibles
        adoptionRepository.deleteAll(activeAdoptions);
        petRepository.deleteAll(availablePets);
        // Las mascotas estan cargadas en  memoria, las borramos de la entidad antes de guardar
        fosterHome.getPets().removeAll(availablePets);
        // Ahora marcamos la casa como inactiva
        fosterHome.setActive(false);
        fosterHomeRepository.save(fosterHome);
        // Forzamos la actualizacion de la base de datos para ver si hay errores, sino perderiamos las imagenes sin que se borrase nada
        fosterHomeRepository.flush();
        // por ultimo borramos las imagenes de las mascotas disponibles que habia en ella
        availablePets.stream()
                            .forEach(pet -> {fileService.deleteImage(pet.getImagePath());});

    }

}
