package com.sparta.easyspring.auth.service;

import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService extends SocialService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String CLIENT_SECRET;

    public KakaoService(UserRepository userRepository, JwtUtil jwtUtil, RestTemplate restTemplate) {
        super(userRepository, jwtUtil, restTemplate);
    }

    @Override
    protected String getSocialName() {
        return "kakao";
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
        return REDIRECT_URI;
    }

    @Override
    protected String getTokenUrl() {
        return "https://kauth.kakao.com";
    }

    @Override
    protected String getTokenPath() {
        return "/oauth/token";
    }

    @Override
    protected String getUserInfoUrl() {
        return "https://kapi.kakao.com";
    }

    @Override
    protected String getUserInfoPath() {
        return "/v2/user/me";
    }
}
