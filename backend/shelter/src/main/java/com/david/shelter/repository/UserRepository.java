package com.david.shelter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.david.shelter.model.entity.User;
import java.util.List;


@Repository 
public interface UserRepository  extends JpaRepository<User,Long>{

    // Estos métodps que buscan por una propiedad estan implementados en JpaRepository,
    public Optional<User> findByEmail(String email);
    public void deleteByEmail(String email);
}
