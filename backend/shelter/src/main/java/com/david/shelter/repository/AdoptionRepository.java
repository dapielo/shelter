package com.david.shelter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.david.shelter.model.entity.Adoption;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.entity.User;


@Repository 
public interface AdoptionRepository extends JpaRepository<Adoption,Long>{

    // Busca todas las adopciones asociadas a una mascota
    public List<Adoption> findByPet(Pet pet);

    // Busca las adopciones asociadas a un Usuario (propuestas de adopcion)
    public List<Adoption> findByAdopter(User adopter);

    // Busca por mascota y usuario que quiere adoptar, es buena practica usar optionals para evitar null
    public Optional<Adoption> findByPetAndAdopter(Pet pet, User adopter);

    // Usamos una consulta JPQL, porque para obtener las adopciones asociadas al usuario propietario de la mascota, tenemos que hacer una union con la tabla
    // de mascotas, y esta con la de hogares de acogida, ya que las adopciones no alacenan en dato del usuario que tiene a la mascota
    @Query("SELECT a FROM Adoption a JOIN a.pet p JOIN p.fosterHome fh JOIN fh.user u WHERE u = :owner")
    public List<Adoption> findByOwner(User owner);

}
