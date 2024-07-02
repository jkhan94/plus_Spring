package com.sparta.easyspring.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.sparta.easyspring.auth.entity.PasswordHistory;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.repository.PasswordHistoryRepository;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.util.JwtUtil;
import com.sparta.easyspring.exception.CustomException;
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
    @DisplayName("회원가입 실패 : 유저 정규식 불만족")
    @Order(10)
    void signup_Should_ThrowException_WhenDoesNotMatchRegexUsername() {
        // given
        AuthRequestDto requestDto = new AuthRequestDto("InvalidUsername", PASSWORD);

        // when - then
        assertThrows(CustomException.class, () -> userService.signup(requestDto));
    }

    @Test
    @DisplayName("회원가입 실패 : 비밀번호 정규식 불만족")
    @Order(11)
    void signup_Should_ThrowException_WhenDoesNotMatchRegesPassword() {
        // given
        AuthRequestDto requestDto = new AuthRequestDto(USERNAME, "InvalidPassword");

        // when - then
        assertThrows(CustomException.class, () -> userService.signup(requestDto));

    }

    @Test
    @DisplayName("회원가입 실패 : 이미 가입한 유저 존재")
    @Order(12)
    void signup_Should_ThrowException_WhenAlreadyExistUser() {
        // given
        AuthRequestDto requestDto = new AuthRequestDto(USERNAME, PASSWORD);

        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        // when - then
        assertThrows(CustomException.class, () -> userService.signup(requestDto));

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
    @DisplayName("로그인 실패 : 유저이름 불일치")
    @Order(13)
    void login_Should_ThrowException_WhenNotMatchUsername() {
        // given
        AuthRequestDto requestDto = new AuthRequestDto(USERNAME, PASSWORD);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.empty());

        // when - then
        assertThrows(CustomException.class, () -> userService.login(requestDto));
    }

    @Test
    @DisplayName("로그인 실패 : 패스워드 불일치")
    @Order(14)
    void login_Should_ThrowException_WhenNotMatchPassword() {
        // given
        AuthRequestDto requestDto = new AuthRequestDto(USERNAME, PASSWORD);
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        // when
        assertThrows(CustomException.class, () -> userService.login(requestDto));
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
    @DisplayName("로그아웃 실패 : 유저이름 불일치")
    @Order(15)
    void logout_Should_ThrowException_WhenNotMatchUsername() {
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.empty());

        // when - then
        assertThrows(CustomException.class, () -> userService.logout(userDetails));

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
    @DisplayName("회원탈퇴 실패 : 유저이름 불일치")
    @Order(16)
    void withdraw_should_ThrowException_WhenNotMatchUsername() {
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.empty());

        // when - then
        assertThrows(CustomException.class, () -> userService.withdraw(userDetails));

    }

    @Test
    @DisplayName("회원탈퇴 실패 : 이미 탈퇴한 유저")
    @Order(17)
    void withdraw_should_ThrowException_WhenAlreadyWithdrawUser() {
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        user.withdraw();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        // when - then
        assertThrows(CustomException.class, () -> userService.withdraw(userDetails));

    }

    @Test
    @DisplayName("비밀번호 수정 성공")
    @Order(5)
    void updatePassword_success() {
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(USERNAME, PASSWORD,
            "123abcAbc!2");

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
    @DisplayName("비밀번호 수정 실패 : 유저이름 불일치")
    @Order(18)
    void updatePassword_Should_ThrowException_WhenNotMatchUsername() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(USERNAME, PASSWORD,
            PASSWORD + "123");
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        // when - then
        assertThrows(CustomException.class, () -> userService.updatePassword(requestDto));
    }

    @Test
    @DisplayName("비밀번호 수정 실패 : 비밀번호 불일치")
    @Order(19)
    void updatePassword_Should_ThrowException_WhenNotMatchPassword() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(USERNAME, PASSWORD,
            PASSWORD + "123");
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        // when - then
        assertThrows(CustomException.class, () -> userService.updatePassword(requestDto));
    }

    @Test
    @DisplayName("비밀번호 수정 실패 : 새 비밀번호 정규식불만족")
    @Order(19)
    void updatePassword_Should_ThrowException_WhenNotMatchRegexNewPassword() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(USERNAME, PASSWORD,
            "InvalidPassword");
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // when - then
        assertThrows(CustomException.class, () -> userService.updatePassword(requestDto));
    }

    @Test
    @DisplayName("비밀번호 수정 실패 : 3회 내 설정했던 비밀번호")
    @Order(20)
    void updatePassword_Should_ThrowException_WhenExistPasswordHistory() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto(USERNAME, PASSWORD,
            PASSWORD + "12");
        User user = createMockUser(1L, USERNAME, PASSWORD);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        ArrayList<PasswordHistory> passwordHistories = new ArrayList<>();
        passwordHistories.add(new PasswordHistory(PASSWORD, user));
        passwordHistories.add(new PasswordHistory(PASSWORD + "12", user));

        given(passwordHistoryRepository.findByUserId(anyLong())).willReturn(passwordHistories);

        // when - then
        assertThrows(CustomException.class, () -> userService.updatePassword(requestDto));

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
        assertEquals(intro, response.getBody().getIntroduction());
    }

    @Test
    @DisplayName("프로필 업데이트 실패 : 유저이름 불일치")
    @Order(21)
    void updateProfile_Should_ThrowException_WhenNotMatchUsername() {
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        UpdateProfileRequestDto requestDto = new UpdateProfileRequestDto(USERNAME + "2", "hello");

        // when - then
        assertThrows(CustomException.class,
            () -> userService.updateProfile(userDetails, requestDto));

    }

    @Test
    @DisplayName("프로필 업데이트 실패 : 유저이름 정규식 불만족")
    @Order(22)
    void updateProfile_Should_ThrowException_WhenNotMatchRegexUsername(){
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        UpdateProfileRequestDto requestDto = new UpdateProfileRequestDto("invalidUsername",
            "hello");

        // when - then
        assertThrows(CustomException.class,
            () -> userService.updateProfile(userDetails, requestDto));
    }

    @Test
    @DisplayName("리프레쉬 성공")
    @Order(7)
    void refresh_success() {
        String refreshToken = "refreshToken";
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(refreshToken);

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
    @DisplayName("리프레쉬 실패 : 유효하지 않은 토큰")
    @Order(23)
    void refresh_Should_ThrowException_WhenInvalidToken(){
        // given
        String refreshToken = "refreshToken";
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(refreshToken);

        given(jwtUtil.validateToken(refreshToken)).willReturn(false);

        // when - then
        assertThrows(CustomException.class, () -> userService.refresh(requestDto));
    }

    @Test
    @DisplayName("리프레쉬 실패 : 유저이름 불일치")
    @Order(24)
    void refresh_Should_ThrowException_WhenNotMatchUsername(){
        // given
        String refreshToken = "refreshToken";
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(refreshToken);

        given(jwtUtil.validateToken(refreshToken)).willReturn(true);

        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        given(jwtUtil.getUsernameFromToken(refreshToken)).willReturn(USERNAME);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.empty());

        // when - then
        assertThrows(CustomException.class,()->userService.refresh(requestDto));

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
        assertEquals(intro, response.getBody().getIntroduction());
    }

    @Test
    @DisplayName("프로필 얻기 실패 : 유저이름 불일치")
    @Order(25)
    void getProfile_Should_ThrowException_WhenNotMatchUsername(){
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when - then
        assertThrows(CustomException.class, () -> userService.getProfile(1L));

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
        assertEquals(3, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());
        assertEquals(3L, response.getBody().get(2).getId());
        assertEquals("hello 1", response.getBody().get(0).getIntroduction());
        assertEquals("hello 2", response.getBody().get(1).getIntroduction());
        assertEquals("hello 3", response.getBody().get(2).getIntroduction());
    }

    @Test
    @DisplayName("모든 프로필 얻기 실패 : 유저 없음 ")
    @Order(26)
    void getProfiles_Should_ThrowException_WhenEmptyUser(){
        // given
        List<User> userList = new ArrayList<>();
        given(userRepository.findAll()).willReturn(userList);

        // when
        ResponseEntity<List<ProfileResponseDto>> response = userService.getProfiles();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
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
