package com.sparta.easyspring.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.easyspring.auth.dto.AuthResponseDto;
import com.sparta.easyspring.auth.dto.KakaoUserInfoDto;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
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

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Social Login")
public class KakaoService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;


    public ResponseEntity<AuthResponseDto> kakaoLogin(String code, HttpServletResponse response)
        throws Exception {

        // 엑세스 토큰 요청
        String accessToken = getToken(code);

        // 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfoDto = getKakaoUserInfo(accessToken);

        // 필요시 회원가입
        User kakaoUser = saveOrUpdateKakaoUser(kakaoUserInfoDto);

        String jwtAccessToken = jwtUtil.createAccessToken(kakaoUser.getUsername());
        String jwtRefreshToken = kakaoUser.getRefreshToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtAccessToken);
        headers.add("Refresh-Token", jwtRefreshToken);
        log.info("카카오 로그인 성공");

        AuthResponseDto responseDto = new AuthResponseDto(kakaoUser.getId(),
            kakaoUser.getUsername());

        return ResponseEntity.ok().body(responseDto);
    }

    public User saveOrUpdateKakaoUser(KakaoUserInfoDto kakaoUserInfoDto) {

        String username = kakaoUserInfoDto.getEmail();
        User user = userRepository.findByUsername(username).orElse(null);

        // 유저가 없으면 생성
        if (user == null) {
            user = new User(username, "kakao", UserRoleEnum.USER);
        }

        String refreshToken = jwtUtil.createRefreshToken(username);
        user.updateRefreshToken(refreshToken);

        return userRepository.save(user);
    }
    private String getToken(String code) throws JsonProcessingException {
        log.info("인가코드: " + code);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
            .fromUriString("https://kauth.kakao.com")
            .path("/oauth/token")
            .encode()
            .build()
            .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "419ee5c620a6a49461823b6ce62866e3");
        body.add("redirect_uri", "http://localhost:8080/api/auth/login/kakao");
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
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("인증 토큰 "+accessToken);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
            .fromUriString("https://kapi.kakao.com")
            .path("/v2/user/me")
            .encode()
            .build()
            .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
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

        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserInfoDto(id, nickname,email);
    }
}
