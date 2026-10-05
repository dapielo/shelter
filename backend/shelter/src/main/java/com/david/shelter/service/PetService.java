package com.david.shelter.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.david.shelter.dto.PetDTO;
import com.david.shelter.exception.PetException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.mapper.PetMapper;
import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.entity.User;
import com.david.shelter.repository.FosterHomeRepository;
import com.david.shelter.repository.PetRepository;
import com.david.shelter.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PetService {

    private final PetRepository petRepository;
    private final FosterHomeRepository fosterHomeRepository;
    private final UserService userService;

    // CREATE
    @Transactional 
    public PetDTO createPet(PetDTO dto) {

        // Comprobamos que el nombre no venga vacío
        if (dto.name().isBlank()) {
            throw new PetException("No se ha podido registrar la mascota, el nombre es obligatorio");
        }
        // Comprobamos que la especie no venga en blanco
        if (dto.species() == null) {
            throw new PetException("No se ha podido registrar la mascota, la especie es obligatoria.");
        }
        // Comprobamos que venga con el id de la casa de acogida
        if (dto.fosterHomeId() == null) {
            throw new PetException("No se ha podido crear la mascota, no trae una casa de acogida asignada");
        } 

        // Buscamos al usuario que hace la peticion
        User user = userService.getUserFromContext();

        // Recordemos que al tener una relacion N:1 con User, el objeto fosterHome viene por defecto con el atributo user cargado con los datos del usuario
        FosterHome fosterHome = fosterHomeRepository.findById(dto.fosterHomeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Error al registrar la mascota, no se ha encontrado una casa de acogida con ese ID"));
        
        // Si el id del usuario que hace la peticion, no coincide con el id del usuario relacionado con el fosterHome que nos indica el dto de la mascota
        // es que la peticion se esta haciendo para una casa de acogida que no pertenece a este usuario (peticion maliciosa). Lanzamos error
        if (fosterHome.getUser().getId().longValue() != user.getId().longValue()) {
            throw new PetException("No se ha registrado la mascota, se esta intentando asignar a una casa de acogida que no le pertenece");
        }

        // Si llegamos hasta aqui, es que todos los datos son correctos, y podemos reguistrar la mascota, hemos diseñado el mapper de forma 
        // que nos pide el fosterHome (no tiene acceso a la capa de servicios ni de repositorios), asi tampoco nos podemos olvidar de asignalo antes de guardar
        try {
            Pet pet = PetMapper.toEntity(dto, fosterHome);
            return PetMapper.toDTO(petRepository.save(pet));
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new PetException("Ha ocurrido un error al guardar la mascota en la base de datos");
        }
    }

    // READ
    // Cualquiera puede leer la informacion de las mascotas, porque la app permite buscar sin estar registrado.
    public PetDTO readPet(Long id) {

        return PetMapper.toDTO(petRepository.findById(id).orElseThrow(() -> new PetException("No existe ninguna mascota con ese id")));
    }

    // UPDATE 
    // igual que con el hogar de acogida, voy a lanzar excepcion si me vienen campos obligaotrios vacíos, creo que es mejor eso que mantener los campos
    // la informacion antigua si me viene vacía, porque en el front puede dar la sensacion de que esta fallando el back si envio un campo vacio que tendría que venir relleno
    @Transactional 
    public void updatePet(PetDTO dto) {

        // Comrpobamos primero que lso 3 campos obligatorios no vengan vacios.
        if (dto.name().isBlank()) {
            throw new PetException("Error al editar la mascota. El campo de nombre no puede venir vacío.");
        }

        if (dto.species() == null) {
            throw new PetException("Error al editar la mascota. El campo de especie no puede venir vacío.");
        }

        if (dto.fosterHomeId() == null) {
            throw new PetException("Error al editar la mascota. El campo de casa de acogida no puede venir vacío.");
        }

        Pet pet = petRepository.findById(dto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("No se ha encontado la mascota con id: " + dto.id()));

        User user = userService.getUserFromContext();
        
        // Nota: como pet esta relacionado N:1 con fosterhome y esta a su vez N:1 con user, la informacion de user me llega en el objeto pet en cascada
        // otra vez debido a que por defecto esrtas relaciones en Spring son EAGER.
        // Si el usuario que hace la peticion no coincide con el usuario con el que esta relacionado la mascota (a traves de la casa de aocgida), la peticion se cancela
        if (user.getId().longValue() != pet.getFosterHome().getUser().getId().longValue()) {
            throw new PetException("No se ha podido modificar la informacion de la mascota, no pertenece a este usuario");
        }

        // Vamos a comporobar que el id de casa de acogida que nos envian del front se corresponde con una casa de acogida del usuario que hace la peticion
        FosterHome newFosterHome = fosterHomeRepository.findById(dto.fosterHomeId())
                                    .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la casa de acogida nueva al actualizar la informacion de la mascota"));
        if (newFosterHome.getUser().getId().longValue() != user.getId().longValue()) {
            throw new PetException("No se ha podido actualizar la mascota, la casa de acogida en la que se intena registrar no pertenece a este usuario");
        }      

        pet.setName(dto.name());
        pet.setSpecies(dto.species());
        pet.setBreed(dto.breed());
        pet.setInfo(dto.info());
        pet.setAgeGroup(dto.ageGroup());
        pet.setFosterHome(newFosterHome);

        try {
            petRepository.save(pet);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new PetException("No se ha podido guardar la informacion actualizada de la mascota en la base de datos");
        }
    }

    // DELETE
    @Transactional 
    public void deletePet(Long id) {

        User user = userService.getUserFromContext();

        Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("No se ha borrado la mascota porque no se encuentra una mascota con ese id en la base de datos"));

        // cancelamos la peticion si la mascota no pertenece al usuario que hace la peticion
        if (pet.getFosterHome().getUser().getId().longValue() != user.getId().longValue()) {
            throw new PetException("No se ha eliminado la mascota porque no pertenece al usuario que esta haciendo la peticion");
        }

        petRepository.delete(pet);
    }

}
