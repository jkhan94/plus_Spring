package com.sparta.easyspring.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        /**
         * PasswordEncoder 인터페이스를 구현한 BCryptPasswordEncoder를 빈으로 등록
         */
        return new BCryptPasswordEncoder();
    }
}
