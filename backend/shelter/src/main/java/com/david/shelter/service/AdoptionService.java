package com.david.shelter.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david.shelter.dto.AdoptionDTO;
import com.david.shelter.exception.AdoptionException;
import com.david.shelter.exception.AdptionAlreadyExistsException;
import com.david.shelter.exception.PetException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.mapper.AdoptionMapper;
import com.david.shelter.model.entity.Adoption;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.entity.User;
import com.david.shelter.model.enums.Status;
import com.david.shelter.repository.AdoptionRepository;
import com.david.shelter.repository.PetRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final UserService userService;
    private final PetRepository petRepository;

    // CREATE 
    @Transactional 
    public AdoptionDTO initiateAdoption(Long petId) {
        
        User user = userService.getUserFromContext();
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new PetException("Mascota con id" + petId + " no econtrada"));

        // Si ya existe una solicitud de adopcion asociada a esta mascota y usuario, es que ya se hizo y lanzamos error
        adoptionRepository.findByPetAndAdopter(pet, user).ifPresent((adoption) -> {throw new AdptionAlreadyExistsException("Ya has hecho una solicitud de adopcion para esta mascota.");});

        // Si no ha saltado la excepcion, podemos crear la solicitud de adopcion
        Adoption newAdoption = new Adoption(
            null, 
            user, 
            pet, 
            LocalDate.now(), 
            null, 
            Status.PENDING);   

        return AdoptionMapper.toDTO(newAdoption);
    }

    // READ (tres casos, el usuario quiere adoptar, el usuario es el que la tiene e historial de adopciones cerradas)
    // Este metodo busca los procesos de adopcion en el que el usuario es el que adopta
    public List<AdoptionDTO> getAdoptionsByAdopter() {

        User user = userService.getUserFromContext();
        List<Adoption> adoptions = adoptionRepository.findByAdopter(user);

        // Las convertimos a DTO antes de enviar y filtramos por las que estan pendientes
        return adoptions.stream()
                        .map(AdoptionMapper::toDTO)
                        .filter(adoption -> adoption.status()==Status.PENDING)
                        .toList();
    }

    // Este metodo busca los procesos de adopcion en el que el usuario es el que tiene la mascota de acogida
    public List<AdoptionDTO> getAdoptionsByOwner() {

        User user = userService.getUserFromContext();
        List<Adoption> adoptions = adoptionRepository.findByOwner(user);

        // Las convertimos a DTO y filtramos por las que estan pendientes
        return adoptions.stream()
                        .map(AdoptionMapper::toDTO)
                        .filter(adoption -> adoption.status() == Status.PENDING)
                        .toList();
    }

    // Este metodo busca las que estan cerradas por si implemento un historial, tanto si es acogedor como adoptante
    public List<AdoptionDTO> getAdoptionsHistory() {

        User user = userService.getUserFromContext();
        List<Adoption> history = new ArrayList<>();

        history.addAll(adoptionRepository.findByOwner(user));
        history.addAll(adoptionRepository.findByAdopter(user));

        return history.stream()
                        .map(AdoptionMapper::toDTO)
                        .filter(adoption -> adoption.status() != Status.PENDING)
                        .toList();
    }

    // UPDATE 
    // Metodo para que un usuario acepte una peticion de adopcion de una mascota a la que tiene de acogida
    @Transactional 
    public void acceptAdoptionPetition(Long adoptionId) {

        User user = userService.getUserFromContext();
        Adoption adoption = adoptionRepository.findById(adoptionId).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado un proceso de adopcion con id: " + adoptionId));

        // Si no coincide el id del usuario dela peticion con el id del usuario que tiene a esta mascota de acogida lanzamos excepcion
        if (adoption.getPet().getFosterHome().getUser().getId().longValue() != user.getId()) {
            throw new AdoptionException("Este usuario no es el que tiene a esta mascota de acogida");
        }

        adoption.setStatus(Status.ACCEPTED);
        adoption.setEndingDate(LocalDate.now());
        adoptionRepository.save(adoption);
    }

    // Igual que el anterior pero para rechazar la peticion, asi no hay que enviar datos (van a ser dos endpoints)
    @Transactional 
    public void declineAdoptionPetition(Long adoptionId) {

        User user = userService.getUserFromContext();
        Adoption adoption = adoptionRepository.findById(adoptionId).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado un proceso de adopcion con id: " + adoptionId));

        // Si no coincide el id del usuario dela peticion con el id del usuario que tiene a esta mascota de acogida lanzamos excepcion
        if (adoption.getPet().getFosterHome().getUser().getId().longValue() != user.getId()) {
            throw new AdoptionException("Este usuario no es el que tiene a esta mascota de acogida");
        }

        adoption.setStatus(Status.DECLINED);
        adoption.setEndingDate(LocalDate.now());
        adoptionRepository.save(adoption);
    }

    // DELETE
    // Por si una persona que hace una peticion para adoptar se arrepiente, o la hace por error, antes de que esta se cierre por la otra parte
    @Transactional 
    public void deleteAdoptionPetition(Pet pet) {

        User user = userService.getUserFromContext();
        Optional<Adoption> adoption = adoptionRepository.findByPetAndAdopter(pet, user);

        // Si exsite, la borramos, si no existe no hacemos nada
        if (adoption.isPresent()) {
            adoptionRepository.delete(adoption.get());
        }
    }
}
