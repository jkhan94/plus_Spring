package com.sparta.easyspring.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.easyspring.auth.dto.UserInfoDto;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j(topic = "Naver Login")
public class NaverService extends SocialService {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String REDIRECT_URL;

    public NaverService(UserRepository userRepository, JwtUtil jwtUtil, RestTemplate restTemplate) {
        super(userRepository, jwtUtil, restTemplate);
    }

    @Override
    protected UserInfoDto getUserInfo(String token) throws JsonProcessingException {
        log.info("인증 토큰 : " + token);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString(getUserInfoUrl())
                .path(getUserInfoPath())
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String nickname = jsonNode.get("response")
                .get("name").asText();
        String email = jsonNode.get("response").get("email").asText();
        return new UserInfoDto(0L, nickname, email);
    }

    @Override
    protected String getSocialName() {
        return "naver";
    }

    @Override
    protected String getClientId() {
        return CLIENT_ID;
    }

    @Override
    protected String getClientSecret() {
        return CLIENT_SECRET;
    }

    @Override
    protected String getredirectUrl() {
        return REDIRECT_URL;
    }

    @Override
    protected String getTokenUrl() {
        return "https://nid.naver.com";
    }

    @Override
    protected String getTokenPath() {
        return "/oauth2.0/token";
    }

    @Override
    protected String getUserInfoUrl() {
        return "https://openapi.naver.com";
    }

    @Override
    protected String getUserInfoPath() {
        return "/v1/nid/me";
    }
}
