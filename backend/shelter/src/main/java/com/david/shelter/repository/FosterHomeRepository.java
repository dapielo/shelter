package com.david.shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.david.shelter.model.entity.FosterHome;

@Repository 
public interface FosterHomeRepository extends JpaRepository<FosterHome,Long> {

}
