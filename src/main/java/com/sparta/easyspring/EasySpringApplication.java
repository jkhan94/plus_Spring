package com.sparta.easyspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class EasySpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasySpringApplication.class, args);
    }

}
