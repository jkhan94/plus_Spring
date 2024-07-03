package com.sparta.easyspring.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.easyspring.auth.dto.AuthResponseDto;
import com.sparta.easyspring.auth.dto.UserInfoDto;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j(topic = "Social Login")
@Service
@RequiredArgsConstructor
public abstract class SocialService {

    protected final UserRepository userRepository;
    protected final JwtUtil jwtUtil;
    protected final RestTemplate restTemplate;

    protected abstract String getSocialName();

    protected abstract String getClientId();

    protected abstract String getClientSecret();

    protected abstract String getredirectUrl();

    protected abstract String getTokenUrl();

    protected abstract String getTokenPath();

    protected abstract String getUserInfoUrl();

    protected abstract String getUserInfoPath();

    public ResponseEntity<AuthResponseDto> login(String code) throws JsonProcessingException {
        // 인가코드 요청
        String accessToken = getToken(code);

        // 토큰으로 카카오 API 호출
        UserInfoDto kakaoUserInfoDto = getUserInfo(accessToken);

        // 필요시 회원가입
        User kakaoUser = updateSocialUser(kakaoUserInfoDto);

        String jwtAccessToken = jwtUtil.createAccessToken(kakaoUser.getUsername());
        String jwtRefreshToken = kakaoUser.getRefreshToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtAccessToken);
        headers.add("Refresh-Token", jwtRefreshToken);

        AuthResponseDto responseDto = new AuthResponseDto(kakaoUser.getId(),
                kakaoUser.getUsername());

        return ResponseEntity.ok().body(responseDto);

    }

    protected String getToken(String code) throws JsonProcessingException {
        log.info("인가 코드 : " + code);

        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString(getTokenUrl())
                .path(getTokenPath())
                .encode()
                .build()
                .toUri();
        log.info("요청 URL : " + uri);
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", getClientId());
        body.add("client_secret", getClientSecret());
        body.add("redirect_uri", getredirectUrl());
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

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
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

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
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        return new UserInfoDto(id, nickname, email);
    }

    protected User updateSocialUser(UserInfoDto userInfoDto) {


        String username = userInfoDto.getEmail();
        User user = userRepository.findByUsername(username).orElse(null);

        // 유저가 없으면 생성
        if (user == null) {
            user = new User(username, getSocialName(), UserRoleEnum.USER);
        }

        String refreshToken = jwtUtil.createRefreshToken(username);
        user.updateRefreshToken(refreshToken);

        return userRepository.save(user);
    }

}
