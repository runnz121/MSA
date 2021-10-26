package com.example.configservicemsa1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServiceMsa1Application {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceMsa1Application.class, args);
    }

}
