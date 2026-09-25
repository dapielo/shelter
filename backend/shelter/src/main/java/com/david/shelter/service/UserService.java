package com.david.shelter.service;

import org.springframework.stereotype.Service;

import com.david.shelter.model.entity.User;
import com.david.shelter.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UserService {

    private final UserRepository userRepository;

    public User getUserByEmail(String username) {
        
        userRepository.findBy
    }
}
