package com.david.shelter.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.Pet;
import com.david.shelter.model.enums.Province;

@Repository 
public interface PetRepository extends JpaRepository<Pet,Long>{

    @Query ("SELECT p FROM Pet p JOIN FETCH p.fosterHome fh WHERE fh.province = :province")
    List<Pet> findByFosterHomeProvince(Province province);

    List<Pet> findByFosterHome(FosterHome fosterHome);

    // Buscamos lso 5 que tiene la fecha de registro mas antigua, es para una de las pantallas del front
    @Query ("SELECT p FROM Pet p ORDER BY p.registeredAt ASC LIMIT 5")
    List<Pet> findOldestRegistered();
}
