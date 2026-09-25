package com.david.shelter.mapper;

import com.david.shelter.dto.UserDTO;
import com.david.shelter.model.entity.User;

// no necesitamos instanciar los mappers para hacer la conversion, asi que vamos a hacer los metodos estaticos
public class UserMapper {

    public static User toEntity(UserDTO dto) {
        
        return new User(
            null, 
            dto.email(), 
            dto.username(), 
            dto.password(), 
            null
        );
    }

    public static UserDTO toDTO(User user) {

        return new UserDTO(
            user.getEmail(), 
            user.getUsername(), 
            null
        );
    }
}
