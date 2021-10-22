package com.example.usersmicroservices1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient //유레카 서버 등록
public class UsersMicroservices1Application {

    public static void main(String[] args) {
        SpringApplication.run(UsersMicroservices1Application.class, args);
    }

    //초기에 기동되는 클래스가 이곳이다 따라서 이곳에 빈을 등록 시켜놓으면 가장먼저 초기화가 된다!
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
