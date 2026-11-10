package com.david.shelter.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.mail.MailException;
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
import com.david.shelter.model.enums.AdoptionStatus;
import com.david.shelter.model.enums.PetStatus;
import com.david.shelter.repository.AdoptionRepository;
import com.david.shelter.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final UserService userService;
    private final PetRepository petRepository;
    private final EmailService emailService;

    // CREATE 
    @Transactional 
    public AdoptionDTO initiateAdoption(Long petId) {
        
        User user = userService.getUserFromContext();
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new PetException("Mascota con id" + petId + " no econtrada"));

        // Comrpobamos que no se intente realizar una adopcion sobre una mascota ya adoptada.
        if (pet.getStatus() == PetStatus.ADOPTED) {
            throw new AdoptionException("Esa mascota ya ha sido adoptada");
        }

        // Comprobamos que el usuario que adopta y el dueño no sean el mismo
        if (user.getId().equals(pet.getFosterHome().getUser().getId())){
            throw new AdoptionException("Estas intentando adoptar una mascota que tienes de acogida");
        }

        // Si ya existe una solicitud de adopcion asociada a esta mascota y usuario, es que ya se hizo y lanzamos error
        adoptionRepository.findByPetAndAdopter(pet, user).ifPresent((adoption) -> {throw new AdptionAlreadyExistsException("Ya has hecho una solicitud de adopcion para esta mascota.");});

        // Si no ha saltado la excepcion, podemos crear la solicitud de adopcion
        Adoption newAdoption = new Adoption(
            null, 
            user, 
            pet, 
            LocalDate.now(), 
            null, 
            AdoptionStatus.PENDING);   

            // Una vez guardada la adopcion, enviamos el email.
            adoptionRepository.save(newAdoption);

            try {
                String text = """
                    Hola %s.
                

                    El usuario %s te ha enviado una solicitud para adoptar a %s. 
                    Entra en la web para aceptar o rechazar la solicitud. Luego espera a que el usuario se ponga en contacto contigo via email.
                    Su direccion de email de contacto es: %s, la puedes consultar en el apartado "Historial" de Shelter.
                

                    Un cordial saludo.
                    El equipo de Shelter.
                    """.formatted(pet.getFosterHome().getUser().getUsername(), user.getUsername(), pet.getName(), user.getEmail());

            emailService.sendEmail(pet.getFosterHome().getUser().getEmail(),"Se te ha enviado una solicitud de adopcion",text);

        } catch (MailException e) {
            log.warn("Email could not be sent", e);
        }
        return AdoptionMapper.toDTO(newAdoption,null);
    }

    // READ (tres casos, el usuario quiere adoptar, el usuario es el que la tiene e historial de adopciones cerradas)
    // Este metodo busca los procesos de adopcion en el que el usuario es el que adopta
    public List<AdoptionDTO> getAdoptionsByAdopter() {

        User user = userService.getUserFromContext();
        List<Adoption> adoptions = adoptionRepository.findByAdopter(user);

        // Las convertimos a DTO antes de enviar y filtramos por las que estan pendientes
        return adoptions.stream()
                        .map((adoption) -> AdoptionMapper.toDTO(adoption,adoption.getPet().getFosterHome().getUser().getEmail()))
                        .filter(adoption -> adoption.status()==AdoptionStatus.PENDING)
                        .toList();
    }

    // Este metodo busca los procesos de adopcion en el que el usuario es el que tiene la mascota de acogida
    public List<AdoptionDTO> getAdoptionsByOwner() {

        User user = userService.getUserFromContext();
        List<Adoption> adoptions = adoptionRepository.findByOwner(user);

        // Las convertimos a DTO y filtramos por las que estan pendientes
        return adoptions.stream()
                        .map((adoption) -> AdoptionMapper.toDTO(adoption, adoption.getAdopter().getEmail()))
                        .filter(adoption -> adoption.status() == AdoptionStatus.PENDING)
                        .toList();
    }

    // Este metodo busca las que estan cerradas por si implemento un historial, tanto si es acogedor como adoptante
    public List<AdoptionDTO> getAdoptionsHistory() {

        User user = userService.getUserFromContext();
        List<AdoptionDTO> history = new ArrayList<>();

        history.addAll(adoptionRepository.findByOwner(user).stream()
                        .map((adoption) -> AdoptionMapper.toDTO(adoption,adoption.getAdopter().getEmail())).toList());

        history.addAll(adoptionRepository.findByAdopter(user).stream()
                        .map((adoption) -> AdoptionMapper.toDTO(adoption, adoption.getPet().getFosterHome().getUser().getEmail())).toList());

        return history.stream()
                        .filter(adoption -> adoption.status() != AdoptionStatus.PENDING)
                        .toList();
    }

    // UPDATE 
    // Metodo para que un usuario acepte una peticion de adopcion de una mascota a la que tiene de acogida
    @Transactional 
    public String acceptAdoptionPetition(Long adoptionId) {

        User user = userService.getUserFromContext();
        Adoption adoption = adoptionRepository.findById(adoptionId).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado un proceso de adopcion con id: " + adoptionId));

        if (adoption.getStatus() != AdoptionStatus.PENDING) {
            throw new AdoptionException("Este proceso de adopcion ha sido cerrado");
        }

        // Si no coincide el id del usuario dela peticion con el id del usuario que tiene a esta mascota de acogida lanzamos excepcion
        if (adoption.getPet().getFosterHome().getUser().getId().longValue() != user.getId()) {
            throw new AdoptionException("Este usuario no es el que tiene a esta mascota de acogida");
        }

        // La mascota tiene que estar disponible
        if (adoption.getPet().getStatus() != PetStatus.AVAILABLE) {
            throw new AdoptionException("Esa mascota ya ha sido adoptada");
        }

        adoption.setStatus(AdoptionStatus.ACCEPTED);
        adoption.setEndingDate(LocalDate.now());
        adoption.getPet().setStatus(PetStatus.ADOPTED);

        // Ahora vamos a buscar las posibles adopciones pendientes por otros usuarios y las vamos a maarcar como DECLINED poruqe se ha aceptado esta
        List<Adoption> autoDeclined = adoptionRepository.findByPet(adoption.getPet()).stream()
                            .filter(others -> others.getStatus() == AdoptionStatus.PENDING && others.getId().longValue() != adoption.getId()).toList();

        autoDeclined.forEach(declined -> {
            declined.setStatus(AdoptionStatus.DECLINED);
            declined.setEndingDate(LocalDate.now());
        });
        adoptionRepository.saveAll(autoDeclined);

        // Al guardar adoption, se actualiza tambien el estado de la mascota.
        adoptionRepository.save(adoption);

        // Enviamos el email
        try {

            String text = """
                Hola %s.
                

                El usuario %s ha aceptado tu solicitud de adopción de %s. Ya puedes ponerte en contacto a través de su email: %s.
                También lo puedes consultar en el apartado Historial de Shelter.
                

                Un cordial saludo.
                El equipo de shelter.
                """.formatted(adoption.getAdopter().getUsername(), adoption.getPet().getFosterHome().getUser().getUsername(), adoption.getPet().getName(), adoption.getPet().getFosterHome().getUser().getEmail());

                emailService.sendEmail(adoption.getAdopter().getEmail(), "Se ha aceptado tu solicitud de adopción", text);

        } catch (MailException e) {
            log.warn("Email could not be sent", e);
        }

        return adoption.getAdopter().getEmail();

        }
            
    // Igual que el anterior pero para rechazar la peticion, asi no hay que enviar datos (van a ser dos endpoints)
    @Transactional 
    public void declineAdoptionPetition(Long adoptionId) {

        User user = userService.getUserFromContext();
        Adoption adoption = adoptionRepository.findById(adoptionId).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado un proceso de adopcion con id: " + adoptionId));
        
        // La adopcion tiene que estar como pendiente
        if (adoption.getStatus() != AdoptionStatus.PENDING) {
            throw new AdoptionException("Este proceso de adopcion ha sido cerrado");
        }

        // Si no coincide el id del usuario dela peticion con el id del usuario que tiene a esta mascota de acogida lanzamos excepcion
        if (adoption.getPet().getFosterHome().getUser().getId().longValue() != user.getId()) {
            throw new AdoptionException("Este usuario no es el que tiene a esta mascota de acogida");
        }

        adoption.setStatus(AdoptionStatus.DECLINED);
        adoption.setEndingDate(LocalDate.now());
        adoptionRepository.save(adoption);

        // Enviamos el email
        try {

            String text = """
                Hola %s.

                Lamentamos decirte que el usuario %s ha rechazado tu solicitud de adopción de %s. Puede que se haya encariñado demasiado consu amigo, o puede que haya encontrado un hogar antes de que haya visto tu mensaje.

                Pero en Shelter sigue habiendo peludetes (o calvetes), esperando por alguien como tú. Vuelve a visitarnos cuando puedas.
                
                
                Un saludo.
                El equipo de Shelter.
                """.formatted(adoption.getAdopter().getUsername(), adoption.getPet().getFosterHome().getUser().getUsername(), adoption.getPet().getName());
            
            emailService.sendEmail(adoption.getAdopter().getEmail(), "Se ha rechazado tu solicitud de adopción.", text);

        } catch (MailException e) {
            log.warn("Email could not be sent.", e);
        }

    }

    // DELETE
    // Por si una persona que hace una peticion para adoptar se arrepiente, o la hace por error, antes de que esta se cierre por la otra parte
    @Transactional 
    public void deleteAdoptionPetition(Long petId) {

        User user = userService.getUserFromContext();

        Pet pet = petRepository.findById(petId).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la mascota con ese id al intentar borrar la peticion de adopcion"));
        Optional<Adoption> adoption = adoptionRepository.findByPetAndAdopter(pet, user);

        // Si exsite y no ha sido respondida por la otra parte, la borramos, si no, no hacemos nada
        if (adoption.isPresent() && adoption.get().getStatus() == AdoptionStatus.PENDING) {
            adoptionRepository.delete(adoption.get());
        }
    }
}
