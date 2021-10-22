package com.example.usersmicroservices1.controller;


import com.example.usersmicroservices1.Service.UserService;
import com.example.usersmicroservices1.dto.UserDto;
import com.example.usersmicroservices1.vo.Greeting;
import com.example.usersmicroservices1.vo.RequestUser;
import com.example.usersmicroservices1.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {

    private Environment env;
    private UserService userService;


    @Autowired
    private Greeting greeting;

    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status(){
        return "It's Wokring in User Service";
    }

//    @GetMapping("/welcome")
//    public String welcome(){
//        return env.getProperty("greeting.message");
//    }

    @GetMapping("/welcome")
    public String welcome(){
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> creatUser(@RequestBody RequestUser user){ //requestUser를 받아서 파라미터로 받음
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //mapper환경설정해주고

        UserDto userDto = mapper.map(user, UserDto.class); //모델 멥퍼로 request user -> userdto로 변환
        userService.createUser(userDto); //서비스로 전달

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class); //response 객체로 반환해서 body에 추가하여 보냄

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }
}
