package com.example.usersmicroservices1.Service;

import com.example.usersmicroservices1.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
}
