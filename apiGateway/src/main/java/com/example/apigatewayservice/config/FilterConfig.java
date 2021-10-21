package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

    //yml 파일에 있는 설정을 자바 코드로 바꿈
    //@Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes() //라우터 등록
                .route(r -> r.path("/first-service/**") //r이라는 값이 전달되면 path를 확인하고
                        .filters(f -> f.addRequestHeader("first-request","first-request-header")
                                .addResponseHeader("first-response","first-response-header")) //filter가 적용된 후
                        .uri("http://localhost:8081")) //이 uri로 전달된다
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request","second-request-header")
                                .addResponseHeader("second-response","second-response-header"))
                        .uri("http://localhost:8082"))
                .build();
    }



}
