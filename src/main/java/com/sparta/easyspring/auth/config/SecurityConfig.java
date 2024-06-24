package com.sparta.easyspring.auth.config;

import com.sparta.easyspring.auth.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        // CSRF 설정
        http.csrf((csrf) -> csrf.disable())
            .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS)) // 세션 관리를 satateless로 설정
            .authorizeHttpRequests((authorizeHttpRequest) ->
                authorizeHttpRequest
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll() // resource접근 허용
                    .requestMatchers("/", "api/auth/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/comments/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/**").hasAnyRole("USER", "ADMIN")
                    .anyRequest().permitAll() // 일단 모든 요청 허용
            )
            .exceptionHandling((exceptionHandling) ->
                exceptionHandling
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        if (request.isUserInRole("BANNED")) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("접근 거부됨 : BAN된 유저입니다.");
                        }
                    })
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
