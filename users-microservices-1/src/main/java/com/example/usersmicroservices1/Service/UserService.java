package com.example.usersmicroservices1.Service;

import com.example.usersmicroservices1.dto.UserDto;
import com.example.usersmicroservices1.jpa.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserById(String userId);

    Iterable<UserEntity> getUserByAll();

}
