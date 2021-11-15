package com.example.usersmicroservices1.controller;


import com.example.usersmicroservices1.Service.UserService;
import com.example.usersmicroservices1.dto.UserDto;
import com.example.usersmicroservices1.jpa.UserEntity;
import com.example.usersmicroservices1.vo.Greeting;
import com.example.usersmicroservices1.vo.RequestUser;
import com.example.usersmicroservices1.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/user-service")
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

//    @GetMapping("/health_check")
//    public String status(){
//        return String.format( "It's Wokring in User Service on PORT %s",env.getProperty("local.server.port"));
//    }

    @GetMapping("/health_check")
    @Timed(value="users.status", longTask = true) //프로메테우스로 전달됨
    public String status(){
        return String.format( "It's Wokring in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", token secret=" + env.getProperty("token.secret")
                + ", token expiration_time=" + env.getProperty("token.expiration_time"));
    }

//    @GetMapping("/welcome")
//    public String welcome(){
//        return env.getProperty("greeting.message");
//    }

    @GetMapping("/welcome")
    @Timed(value="users.welcome", longTask = true)
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


    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {                 //v가 들어왔을떄( 리스트 안에 있는 각 데이터 하나 값 어떻게 할지 {} 안에 구현하ㅐ서 쓰면됨
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });


        return ResponseEntity.status(HttpStatus.OK).body(result);

    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){

        UserDto userDto = userService.getUserById(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);

    }
}
