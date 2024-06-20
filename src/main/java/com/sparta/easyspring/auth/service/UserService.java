package com.sparta.easyspring.auth.service;

import com.sparta.easyspring.auth.dto.AuthRequestDto;
import com.sparta.easyspring.auth.dto.RefreshTokenRequestDto;
import com.sparta.easyspring.auth.dto.UpdatePasswordRequestDto;
import com.sparta.easyspring.auth.dto.UpdateProfileRequestDto;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.entity.UserStatus;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.util.JwtUtil;
import java.util.Optional;

import com.sparta.easyspring.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.sparta.easyspring.exception.ErrorEnum.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final String USERID_REGEX = "^[a-z0-9]{4,10}$";
    private final String USERPASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:'\",.<>/?]).{8,15}$";

    /**
     * badRequest리턴값들은 나중에 예외처리로 분리할 예정
     *
     * @param signupRequest
     * @return
     */

    public ResponseEntity<String> signup(AuthRequestDto signupRequest) {
        String authName = signupRequest.getUsername();
        String password = signupRequest.getPassword();

        if (!authName.matches(USERID_REGEX)) {
            return ResponseEntity.badRequest()
                .body("아이디는 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다.");
        }

        if (!password.matches(USERPASSWORD_REGEX)) {
            return ResponseEntity.badRequest()
                .body("최소 8자 이상, 15자 이하이며 알파벳 대소문자(az, AZ), 숫자(0~9),특수문자로 구성되어야 합니다.");
        }

        Optional<User> invalidUser = userRepository.findByUsername(authName);
        if (invalidUser.isPresent()) {
            return ResponseEntity.badRequest().body("중복된 사용자 ID가 존재합니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(authName, encodedPassword, UserRoleEnum.USER);
        userRepository.save(user);

        return ResponseEntity.ok("회원가입 성공");
    }

    public ResponseEntity<String> login(AuthRequestDto loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("유저를 찾을 수 없습니다.");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("로그인 실패 : 사용자 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Refresh-Token", refreshToken);

        return new ResponseEntity<>("로그인 성공 : " + user.getUsername(), headers, HttpStatus.OK);
    }

    public ResponseEntity<String> logout(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("유저를 찾을 수 없습니다.");
        }

        user.clearRefreshToken();
        userRepository.save(user);

        return ResponseEntity.ok("로그아웃 성공 : " + username);
    }

    public ResponseEntity<String> withdraw(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("유저를 찾을 수 없습니다.");
        }

        if (UserStatus.WITHDRAW == user.getUserStatus()) {
            return ResponseEntity.badRequest().body("회원탈퇴 실패 : 이미 탈퇴한 회원");
        }

        user.withdraw();
        user.clearRefreshToken();
        userRepository.save(user);

        return ResponseEntity.ok("회원탈퇴 성공");
    }

    public ResponseEntity<String> updatePassword(UpdatePasswordRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("유저를 찾을 수 없습니다");
        }
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(requestDto.getNewpassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("동일한 비밀번호로는 변경할 수 없습니다.");
        }
        String newPassword = passwordEncoder.encode(requestDto.getNewpassword());
        user.updatePassword(newPassword);
        userRepository.save(user);

        return ResponseEntity.ok("비밀번호 변경 성공");
    }

    public ResponseEntity<String> updateProfile(UserDetailsImpl userDetails,
        UpdateProfileRequestDto requestDto) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("유저를 찾을 수 없습니다.");
        }

        String updateName = requestDto.getUsername();
        if (updateName.matches(USERID_REGEX)) {
            return ResponseEntity.badRequest()
                .body("아이디는 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다.");
        }

        String updateIntroduction = requestDto.getIntroduction();

        user.updateUsername(updateName);
        user.updateIntroduction(updateIntroduction);
        userRepository.save(user);

        // 새 토큰 발급
        String accessToken = jwtUtil.createAccessToken(updateName);
        String refreshToken = jwtUtil.createRefreshToken(updateName);
        user.updateRefreshToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Refresh-Token", refreshToken);

        return new ResponseEntity<>("프로필 변경 성공 : " + updateName, headers, HttpStatus.OK);
    }

    public ResponseEntity<String> refresh(RefreshTokenRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body("유효하지않은 Refresh Token입니다.");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("유저를 찾을 수 없습니다.");
        }

        if (!refreshToken.equals(user.getRefreshToken())) {
            return ResponseEntity.badRequest().body("Refresh Token이 일치하지 않습니다.");
        }

        // AccessToken 재발급
        String newAccessToken = jwtUtil.createAccessToken(username);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);

        return new ResponseEntity<>("Refresh Token 재발급", headers, HttpStatus.OK);
    }

    public User findById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public User findUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }
}
