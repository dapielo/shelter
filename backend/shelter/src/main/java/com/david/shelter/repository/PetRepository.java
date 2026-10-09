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
}
