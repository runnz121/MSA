package com.example.usersmicroservices1;


import com.example.usersmicroservices1.error.FeignErrorDecoder;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;



@SpringBootApplication
@EnableDiscoveryClient //유레카 서버 등록
@EnableFeignClients //feign 클라이언트 사용
public class UsersMicroservices1Application {

    public static void main(String[] args) {
        SpringApplication.run(UsersMicroservices1Application.class, args);
    }

    //초기에 기동되는 클래스가 이곳이다 따라서 이곳에 빈을 등록 시켜놓으면 가장먼저 초기화가 된다!
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //Feign client 사용으로 주석 처리

    //빈으로 resttemplate 등록
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    //Fegin client 예외처리 로깅을 위한 빈등록
    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

    //예외처리 코드 빈 주입
//    @Bean
//    public FeignErrorDecoder getFeignErrorDecoder() {
//        return new FeignErrorDecoder();
//    }
}
