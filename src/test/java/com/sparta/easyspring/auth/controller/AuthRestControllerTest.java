package com.sparta.easyspring.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.easyspring.auth.config.SecurityConfig;
import com.sparta.easyspring.auth.dto.AuthRequestDto;
import com.sparta.easyspring.auth.dto.AuthResponseDto;
import com.sparta.easyspring.auth.dto.RefreshTokenRequestDto;
import com.sparta.easyspring.auth.dto.UpdatePasswordRequestDto;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.security.UserDetailsServiceImpl;
import com.sparta.easyspring.auth.service.KakaoService;
import com.sparta.easyspring.auth.service.NaverService;
import com.sparta.easyspring.auth.service.UserService;
import com.sparta.easyspring.auth.util.JwtUtil;
import com.sparta.easyspring.config.MockSpringSecurityFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.lang.reflect.Field;
import java.security.Principal;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = AuthRestController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
@TestInstance(Lifecycle.PER_CLASS)
public class AuthRestControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    UserService userService;

    @MockBean
    KakaoService kakaoService;

    @MockBean
    NaverService naverService;

    @MockBean
    RestTemplate restTemplate;

    @MockBean
    UserRepository userRepository;

    @MockBean
    JwtUtil jwtUtil;

    String USERNAME = "seokjoon123";
    String PASSWORD = "123Asd!@#";

    @BeforeAll
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity(new MockSpringSecurityFilter()))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        mockPrincipal = new UsernamePasswordAuthenticationToken(user, "",
                userDetails.getAuthorities());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception {
        // given

        AuthRequestDto requestDto = new AuthRequestDto(USERNAME, PASSWORD);
        AuthResponseDto responseDto = new AuthResponseDto(1L, USERNAME);

        ResponseEntity<AuthResponseDto> result = new ResponseEntity<>(responseDto, HttpStatus.OK);

        given(userService.signup(any())).willReturn(result);

        // when - then
        mvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andDo(print());

    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        // given

        AuthRequestDto requestDto = new AuthRequestDto(USERNAME, PASSWORD);
        AuthResponseDto responseDto = new AuthResponseDto(1L, USERNAME);

        String accessToken = "Bearer accessToken";
        String refreshToken = "refershToken";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.add("Refresh-Token", refreshToken);

        ResponseEntity<AuthResponseDto> result = new ResponseEntity<>(responseDto, headers,
                HttpStatus.OK);

        given(userService.login(any())).willReturn(result);

        // when - then

        mvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(header().stringValues(accessToken))
                .andExpect(header().stringValues(refreshToken))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() throws Exception {
        // given

        AuthResponseDto responseDto = new AuthResponseDto(1L, USERNAME);

        given(userService.logout(any())).willReturn(ResponseEntity.ok(responseDto));

        // when - then

        mvc.perform(post("/api/auth/logout")
                        .principal(mockPrincipal)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andDo(print());
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void withdraw_success() throws Exception {
        // given

        AuthResponseDto responseDto = new AuthResponseDto(1L, USERNAME);

        given(userService.withdraw(any())).willReturn(ResponseEntity.ok(responseDto));

        // when - then

        mvc.perform(delete("/api/auth/withdraw")
                        .principal(mockPrincipal)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 수정 성공")
    void updatePassword_success() throws Exception {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(USERNAME, PASSWORD,
                PASSWORD + "123");
        try {
            Field usernameFiled = UpdatePasswordRequestDto.class.getDeclaredField("username");
            usernameFiled.setAccessible(true);
            usernameFiled.set(requestDto, USERNAME);

            Field passwordField = UpdatePasswordRequestDto.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(requestDto, PASSWORD);

            Field newPasswordField = UpdatePasswordRequestDto.class.getDeclaredField("newpassword");
            newPasswordField.setAccessible(true);
            newPasswordField.set(requestDto, "Newps123!@#");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        AuthResponseDto responseDto = new AuthResponseDto(1L, USERNAME);

        given(userService.updatePassword(any())).willReturn(ResponseEntity.ok(responseDto));

        // when - then
        mvc.perform(put("/api/auth//update/password")
                        .principal(mockPrincipal)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andDo(print());
    }

    @Test
    @DisplayName("리프레시 토큰")
    void refresh_success() throws Exception {
        // given
        String accessToken = "Bearer accesToken";
        String refreshToken = "refreshToken123";
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);

        String response = "Refresh Token 재발급";

        given(userService.refresh(any())).willReturn(
                new ResponseEntity<>(response, headers, HttpStatus.OK));

        // when - then
        mvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Refresh Token 재발급"))
                .andDo(print());
    }

    @Test
    @DisplayName("카카오 로그인 성공")
    void kakaoLogin_success() throws Exception {
        // given

        String code = "kakaoCode";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        AuthResponseDto responseDto = new AuthResponseDto(1L, USERNAME);

        given(kakaoService.login(any())).willReturn(ResponseEntity.ok(responseDto));

        // when - then
        mvc.perform(get("/api/auth/login/kakao")
                        .params(params)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andDo(print());
    }


    @Test
    @DisplayName("네이버 로그인 성공")
    void naverLogin_success() throws Exception {
        // given

        String code = "naverCode";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        AuthResponseDto responseDto = new AuthResponseDto(1L, USERNAME);

        given(naverService.login(any())).willReturn(ResponseEntity.ok(responseDto));

        // when - then
        mvc.perform(get("/api/auth/login/naver")
                        .params(params)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andDo(print());
    }


}
