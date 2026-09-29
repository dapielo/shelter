package com.david.shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.User;

import java.util.List;


@Repository 
public interface FosterHomeRepository extends JpaRepository<FosterHome,Long> {

    // HAY QUE CAMBIAR ESTA CONSULTA, HAY QUE USAR SPQL EN LUGAR DE SQL
    - 
    @Query ("select f from foster_homes f left join pets p on p.fosterHome_id = f.id")
    public List<FosterHome> findByUserWithPets(User user);

}
