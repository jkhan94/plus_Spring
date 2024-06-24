package com.sparta.easyspring.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import com.sparta.easyspring.auth.dto.AuthRequestDto;
import com.sparta.easyspring.auth.dto.AuthResponseDto;
import com.sparta.easyspring.auth.dto.ProfileResponseDto;
import com.sparta.easyspring.auth.dto.RefreshTokenRequestDto;
import com.sparta.easyspring.auth.dto.UpdatePasswordRequestDto;
import com.sparta.easyspring.auth.dto.UpdateProfileRequestDto;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.repository.PasswordHistoryRepository;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.util.JwtUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
//@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {


    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordHistoryRepository passwordHistoryRepository;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    PasswordEncoder passwordEncoder;

    String USERNAME = "seokjoon1";
    String PASSWORD = "123abcAbc!1";


    @Test
    @DisplayName("회원가입 성공")
    @Order(1)
    void signup_success() {
        // given
        AuthRequestDto requestDto = new AuthRequestDto(USERNAME, PASSWORD);

        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);

        given(userRepository.save(any(User.class))).willReturn(user);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(PASSWORD)).willReturn("encodedPassword");

        // when
        ResponseEntity<AuthResponseDto> response = userService.signup(requestDto);

        // then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getUsername(), USERNAME);
    }

    @Test
    @DisplayName("로그인 성공")
    @Order(2)
    void login_success() {
        //given
        AuthRequestDto requestDto = new AuthRequestDto(USERNAME, PASSWORD);

        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        given(jwtUtil.createAccessToken(anyString())).willReturn(accessToken);
        given(jwtUtil.createRefreshToken(anyString())).willReturn(refreshToken);

        // when
        ResponseEntity<AuthResponseDto> response = userService.login(requestDto);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<String> headers = response.getHeaders().get("Authorization");
        assertEquals(1, headers.size());
        assertEquals("Bearer " + accessToken, headers.get(0));

        assertEquals(USERNAME, Objects.requireNonNull(response.getBody()).getUsername());
    }

    @Test
    @DisplayName("로그아웃 성공")
    @Order(3)
    void logout_success() {
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        // when
        ResponseEntity<AuthResponseDto> response = userService.logout(userDetails);

        // then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(USERNAME, response.getBody().getUsername());
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    @Order(4)
    void withdraw_success() {
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        // when
        ResponseEntity<AuthResponseDto> response = userService.withdraw(userDetails);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(USERNAME, response.getBody().getUsername());
    }

    @Test
    @DisplayName("비밀번호 수정 성공")
    @Order(5)
    void updatePassword_success() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto();
        try {
            Field usernameFiled = UpdatePasswordRequestDto.class.getDeclaredField("username");
            usernameFiled.setAccessible(true);
            usernameFiled.set(requestDto, USERNAME);

            Field passwordField = UpdatePasswordRequestDto.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(requestDto, PASSWORD);

            Field newPasswordField = UpdatePasswordRequestDto.class.getDeclaredField("newpassword");
            newPasswordField.setAccessible(true);
            newPasswordField.set(requestDto, "123abcAbc!2");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // when
        ResponseEntity<AuthResponseDto> response = userService.updatePassword(requestDto);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(USERNAME, response.getBody().getUsername());
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    @Order(6)
    void updateProfile_success() {
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String intro = "Hello World";
        UpdateProfileRequestDto requestDto = new UpdateProfileRequestDto(USERNAME, intro);

        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        given(jwtUtil.createAccessToken(USERNAME)).willReturn(accessToken);
        given(jwtUtil.createRefreshToken(USERNAME)).willReturn(refreshToken);

        // when
        ResponseEntity<ProfileResponseDto> response = userService.updateProfile(userDetails,
            requestDto);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(USERNAME, response.getBody().getUsername());
        assertEquals(intro, response.getBody().getIndroduction());
    }

    @Test
    @DisplayName("리프레쉬 성공")
    @Order(7)
    void refresh_success() {
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto();
        String refreshToken = "refreshToken";
        try {
            Field field = RefreshTokenRequestDto.class.getDeclaredField("refreshToken");
            field.setAccessible(true);
            field.set(requestDto, refreshToken);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        given(jwtUtil.validateToken(refreshToken)).willReturn(true);
        given(jwtUtil.getUsernameFromToken(refreshToken)).willReturn(USERNAME);

        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        user.updateRefreshToken(refreshToken);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        String accessToken = "accessToken";
        given(jwtUtil.createAccessToken(USERNAME)).willReturn(accessToken);

        // when
        ResponseEntity<String> response = userService.refresh(requestDto);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<String> headers = response.getHeaders().get("Authorization");
        assert headers != null;
        assertEquals("Bearer " + accessToken, headers.get(0));
        assertEquals("Refresh Token 재발급", response.getBody());
    }

    @Test
    @DisplayName("프로필 얻기 성공")
    @Order(8)
    void getProfile_success() {
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, 1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        String intro = "Hello World";
        user.updateIntroduction(intro);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        ResponseEntity<ProfileResponseDto> response = userService.getProfile(1L);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(USERNAME, response.getBody().getUsername());
        assertEquals(1L, response.getBody().getId());
        assertEquals(intro, response.getBody().getIndroduction());
    }


    @Test
    @DisplayName("모든 프로필 얻기")
    @Order(9)
    void getProfiles() {
        // given
        User user1 = createMockUser(1L, USERNAME + "1", PASSWORD);
        User user2 = createMockUser(2L, USERNAME + "2", PASSWORD);
        User user3 = createMockUser(3L, USERNAME + "3", PASSWORD);
        user1.updateIntroduction("hello 1");
        user2.updateIntroduction("hello 2");
        user3.updateIntroduction("hello 3");

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        given(userRepository.findAll()).willReturn(userList);

        // when
        ResponseEntity<List<ProfileResponseDto>> response = userService.getProfiles();

        // then
        assertEquals(3,response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());
        assertEquals(3L, response.getBody().get(2).getId());
        assertEquals("hello 1", response.getBody().get(0).getIndroduction());
        assertEquals("hello 2", response.getBody().get(1).getIndroduction());
        assertEquals("hello 3", response.getBody().get(2).getIndroduction());
    }

    /**
     * Reflection으로 Id 필드 설정
     *
     * @param id
     * @param username
     * @param password
     * @return
     */
    private User createMockUser(Long id, String username, String password) {
        User user = new User(username, password, UserRoleEnum.USER);
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
