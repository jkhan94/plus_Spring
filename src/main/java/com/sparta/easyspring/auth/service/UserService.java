package com.sparta.easyspring.auth.service;

import com.sparta.easyspring.auth.dto.AuthRequestDto;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.entity.UserStatus;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.util.JwtUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    private final String USERID_REGEX ="^[a-z0-9]{4,10}$";
    private final String USERPASSWORD_REGEX ="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:'\",.<>/?]).{8,15}$";

    /**
     * badRequest리턴값들은 나중에 예외처리로 분리할 예정
     * @param signupRequest
     * @return
     */
    public ResponseEntity<String> signup(AuthRequestDto signupRequest) {
        String authName = signupRequest.getUsername();
        String password = signupRequest.getPassword();

        if(!authName.matches(USERID_REGEX)){
            return ResponseEntity.badRequest().body("아이디는 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다.");
        }

        if (!password.matches(USERPASSWORD_REGEX)){
            return ResponseEntity.badRequest().body("최소 8자 이상, 15자 이하이며 알파벳 대소문자(az, AZ), 숫자(0~9),특수문자로 구성되어야 합니다.");
        }

        Optional<User> validUser = userRepository.findByUsername(authName);
        if(validUser.isPresent()){
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
            return ResponseEntity.badRequest().body("로그인 실패 : 사용자 ID가 일치하지 않습니다.");
        }
        if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            return ResponseEntity.badRequest().body("로그인 실패 : 사용자 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Refresh-Token", refreshToken);

        return new ResponseEntity<>("로그인 성공 : "+user.getUsername(),headers, HttpStatus.OK);
    }

    public ResponseEntity<String> logout(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElse(null);
        if(user ==null){
            return ResponseEntity.badRequest().body("로그아웃 실패 ");
        }

        user.clearRefreshToken();
        userRepository.save(user);

        return ResponseEntity.ok("로그아웃 성공 : "+username);
    }

    public ResponseEntity<String> withdraw(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElse(null);

        if(user ==null){
            return ResponseEntity.badRequest().body("회원탈퇴 실패 : 유저를 찾을 수 없습니다.");
        }

        if (UserStatus.WITHDRAW == user.getUserStatus()) {
            return ResponseEntity.badRequest().body("회원탈퇴 실패 : 이미 탈퇴한 회원");
        }

        user.withdraw();
        user.clearRefreshToken();

        userRepository.save(user);
        return ResponseEntity.ok("회원탈퇴 성공");
    }
}
