package com.david.shelter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.david.shelter.dto.PetDTO;
import com.david.shelter.exception.PetException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.mapper.PetMapper;
import com.david.shelter.model.entity.Adoption;
import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.entity.User;
import com.david.shelter.model.enums.PetStatus;
import com.david.shelter.model.enums.Province;
import com.david.shelter.repository.AdoptionRepository;
import com.david.shelter.repository.FosterHomeRepository;
import com.david.shelter.repository.PetRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PetService {

    private final PetRepository petRepository;
    private final FosterHomeRepository fosterHomeRepository;
    private final UserService userService;
    private final FileService fileService;
    private final AdoptionRepository adoptionRepository;

    // CREATE
    @Transactional 
    public PetDTO createPet(PetDTO dto, MultipartFile image) {

        // Comprobamos que el nombre no venga vacío
        if (dto.name() == null || dto.name().isBlank()) {
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
        Pet pet = PetMapper.toEntity(dto, fosterHome);
        String imagePath = fileService.storeImage(image, user);
        pet.setImagePath(imagePath);

        try {
            return PetMapper.toDTO(petRepository.saveAndFlush(pet));
        } catch (RuntimeException e) {
            // Si hay algun error borramos la imagen para que no quede huerfana
            fileService.deleteImage(imagePath);
            throw new PetException("Ha ocurrido un error al guardar la mascota en la base de datos");
        }
    }

    // READ
    // Cualquiera puede leer la informacion de las mascotas, porque la app permite buscar sin estar registrado.
    public PetDTO getPetById(Long id) {

        return PetMapper.toDTO(petRepository.findById(id).orElseThrow(() -> new PetException("No existe ninguna mascota con ese id")));
    }

    // para obtener las mascotas en una provincia para los filtros de la aplicacion, solo para las disponibles
    public List<PetDTO> findByProvince(Province province) {
        
        return petRepository.findByFosterHomeProvince(province)
                .stream()
                .filter(pet -> pet.getStatus() == PetStatus.AVAILABLE)
                .map(PetMapper::toDTO)
                .toList();
    }

    // Devolvemos los 5 que llevan mas tiempo sin adoptar
    public List<PetDTO> getVeterans() {
        return petRepository.findOldestRegistered().stream()
                    .map(PetMapper::toDTO)
                    .filter(pet -> pet.status() == PetStatus.AVAILABLE)
                    .toList();
    }

    // UPDATE 
    // igual que con el hogar de acogida, voy a lanzar excepcion si me vienen campos obligaotrios vacíos, creo que es mejor eso que mantener los campos
    // la informacion antigua si me viene vacía, porque en el front puede dar la sensacion de que esta fallando el back si envio un campo vacio que tendría que venir relleno
    @Transactional 
    public PetDTO updatePet(PetDTO dto, MultipartFile image) {

        // Si no se comprueba en findById del repository lanza un error antes de buscar.
        if (dto.id() == null) {
            throw new PetException("Hay que indicar el id de la mascota");
        }

        // Comrpobamos primero que lso 3 campos obligatorios no vengan vacios.
        if (dto.name() == null || dto.name().isBlank()) {
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
        
        // Extraemos la antigua ruta de la imagen (OJO PUEDE SER NULL)
        String oldImagePath = pet.getImagePath();
        String newImagePath = null;

        // Si recibimos un MultipartFile no nulo y no vacio, reemplazamos la imagen por la nueva, y cambiamos la ruta en la entidad
        if (image != null && !image.isEmpty()) {
            // Tenemos controlado el imagePath == null en los propios metodos del FileService, tanto en update como en delete, asi que no salta excepcion
            newImagePath = fileService.storeImage(image, user);
            pet.setImagePath(newImagePath);
        }

        try {
            
            PetDTO updated = PetMapper.toDTO(petRepository.saveAndFlush(pet));

            if (newImagePath != null) {
                // Si se ha reemplazado borramos la vieja que ha quedado huerfana
                fileService.deleteImage(oldImagePath);
            }

            return updated;

        } catch (RuntimeException e) {
            // Si hay un error borramos la imagen poruqe quedaria huerfana
            fileService.deleteImage(newImagePath);
            throw new PetException("No se ha podido guardar la informacion actualizada de la mascota en la base de datos");
        }
    }

    @Transactional 
    public void deleteImage(Long id) {

        Pet pet = petRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No hay una mascota con ese id"));
        User user = userService.getUserFromContext();

        if (pet.getFosterHome().getUser().getId().longValue() != user.getId().longValue()) {
            throw new PetException("La mascota no pertenece a este usuario");
        }

        String imagePath = pet.getImagePath();
        
        // Tenemos que hacer flush para forzar la actualizacion en la BD y ver que no salte un error angtes de borrar la imagen
        try{
            pet.setImagePath(null);
            petRepository.saveAndFlush(pet);
            fileService.deleteImage(imagePath);
        } catch (RuntimeException e) {
            throw new PetException("No se ha podido eliminar la imagen de la mascota");
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

        // Despues de borrar la mascota, borramos la imagen, al tener anotado el metodo como transactional, si falla la primera no se borra la segunda
        // pero para ello tenemos uqe hacer un flush, que es lo que hace el cambio en la BD, el delete solo lo guarda en cache
        String imagePath = pet.getImagePath();

        // Primero borramos las adociones asociadas a la mascota, poruqe si existe alguna el borrado fallará
        List<Adoption> adoptionsByPet = adoptionRepository.findByPet(pet);
        adoptionRepository.deleteAll(adoptionsByPet);

        // Antes de borrar la mascota tenemos que quitarla de la foster home, esto se debe a que hemos marcado la relacion entre fosterhome y pet como eager
        // y eso hace que la mascota este cargada en memoria tanto en la variable pet como en la variable fosterHome y eso puede dar lugar a error
        pet.getFosterHome().getPets().remove(pet);
        petRepository.delete(pet);
        petRepository.flush();
        fileService.deleteImage(imagePath);

    }

}
