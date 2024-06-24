package com.sparta.easyspring.auth.service;

import com.sparta.easyspring.auth.dto.AuthRequestDto;
import com.sparta.easyspring.auth.dto.AuthResponseDto;
import com.sparta.easyspring.auth.dto.ProfileResponseDto;
import com.sparta.easyspring.auth.dto.RefreshTokenRequestDto;
import com.sparta.easyspring.auth.dto.UpdatePasswordRequestDto;
import com.sparta.easyspring.auth.dto.UpdateProfileRequestDto;
import com.sparta.easyspring.auth.entity.PasswordHistory;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.entity.UserStatus;
import com.sparta.easyspring.auth.repository.PasswordHistoryRepository;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.util.JwtUtil;
import com.sparta.easyspring.exception.CustomException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.sparta.easyspring.exception.ErrorEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "User service")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final JwtUtil jwtUtil;

    private final String USERID_REGEX = "^[a-z0-9]{4,10}$";
    private final String USERPASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:'\",.<>/?]).{8,15}$";

    public ResponseEntity<AuthResponseDto> signup(AuthRequestDto signupRequest) {
        log.info("signup 시작");
        String authName = signupRequest.getUsername();
        String password = signupRequest.getPassword();

        log.info("유저 정규식");
        if (!authName.matches(USERID_REGEX)) {
            throw new CustomException(INVALID_USERNAME);
        }

        log.info("비밀번호 정규식");
        if (!password.matches(USERPASSWORD_REGEX)) {
            throw new CustomException(INVALID_PASSWORD);
        }

        log.info("중복이름 검색");
        Optional<User> invalidUser = userRepository.findByUsername(authName);
        if (invalidUser.isPresent()) {
            throw new CustomException(DUPLICATE_USER);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(authName, encodedPassword, UserRoleEnum.USER);
        User savedUser = userRepository.save(user);

        PasswordHistory ph = new PasswordHistory(encodedPassword, savedUser);
        passwordHistoryRepository.save(ph);

        AuthResponseDto responseDto = new AuthResponseDto(savedUser.getId(), savedUser.getUsername());

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AuthResponseDto> login(AuthRequestDto loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

        if (user == null) {
            throw new CustomException(USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(INCORRECT_PASSWORD);
        }
        if (user.getUserRole().equals(UserRoleEnum.BANNED)) {
            throw new CustomException(BANNED_USER);
        }

        String accessToken = jwtUtil.createAccessToken(user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Refresh-Token", refreshToken);

        AuthResponseDto responseDto = new AuthResponseDto(user.getId(), user.getUsername());

        return new ResponseEntity<>(responseDto,headers,HttpStatus.OK);
    }

    public ResponseEntity<AuthResponseDto> logout(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new CustomException(USER_NOT_FOUND);
        }

        user.clearRefreshToken();
        userRepository.save(user);

        AuthResponseDto responseDto = new AuthResponseDto(user.getId(), user.getUsername());

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AuthResponseDto> withdraw(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new CustomException(USER_NOT_FOUND);
        }

        if (UserStatus.WITHDRAW == user.getUserStatus()) {
            throw new CustomException(WITHDRAW_USER);
        }

        user.withdraw();
        user.clearRefreshToken();
        userRepository.save(user);

        AuthResponseDto responseDto = new AuthResponseDto(user.getId(), user.getUsername());

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AuthResponseDto> updatePassword(UpdatePasswordRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElse(null);

        if (user == null) {
            throw new CustomException(USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(INCORRECT_PASSWORD);
        }
        if (!requestDto.getNewpassword().matches(USERPASSWORD_REGEX)) {
            throw new CustomException(INVALID_PASSWORD);
        }

        List<PasswordHistory> passwordHisList = passwordHistoryRepository.findByUserId(
            user.getId());

        for (var ph : passwordHisList) {
            if (passwordEncoder.matches(requestDto.getNewpassword(), ph.getPassword())) {
                throw new CustomException(PASSWORD_CHANGE_NOT_ALLOWED);
            }
        }

        if (passwordHisList.size() >= 3) {
            PasswordHistory delPassword = passwordHisList.stream()
                .min(Comparator.comparing(PasswordHistory::getCreatedAt)).orElseThrow();
            passwordHistoryRepository.delete(delPassword);
        }

        String encodedNewPassword = passwordEncoder.encode(requestDto.getNewpassword());

        PasswordHistory ph = new PasswordHistory(encodedNewPassword, user);
        passwordHistoryRepository.save(ph);

        user.updatePassword(encodedNewPassword);
        userRepository.save(user);

        AuthResponseDto responseDto = new AuthResponseDto(user.getId(), user.getUsername());

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<ProfileResponseDto> updateProfile(UserDetailsImpl userDetails,
        UpdateProfileRequestDto requestDto) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (user == null) {
            throw new CustomException(USER_NOT_FOUND);
        }

        String updateName = requestDto.getUsername();
        if (updateName.matches(USERID_REGEX)) {
            throw new CustomException(INVALID_USERNAME);
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

        ProfileResponseDto responseDto = new ProfileResponseDto(user.getId(), user.getUsername(),
            user.getIntroduction());

        return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
    }

    public ResponseEntity<String> refresh(RefreshTokenRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(INVALID_TOKEN);
        }


        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new CustomException(USER_NOT_FOUND);
        }

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new CustomException(INVALID_TOKEN);
        }

        // AccessToken 재발급
        String newAccessToken = jwtUtil.createAccessToken(username);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);

        return new ResponseEntity<>("Refresh Token 재발급", headers, HttpStatus.OK);
    }


    public User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    public ResponseEntity<ProfileResponseDto> getProfile(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Long userId = user.getId();
        String username = user.getUsername();
        String introduction = user.getIntroduction();

        ProfileResponseDto responseDto = new ProfileResponseDto(userId, username, introduction);

        return ResponseEntity.ok().body(responseDto);
    }

    public ResponseEntity<List<ProfileResponseDto>> getProfiles() {
        List<User> userList = userRepository.findAll();

        if (userList.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        List<ProfileResponseDto> responseDtoList = new ArrayList<>();
        for (User user : userList) {
            Long userid = user.getId();
            String username = user.getUsername();
            String introduction = user.getIntroduction();

            responseDtoList.add(new ProfileResponseDto(userid, username, introduction));
        }

        return ResponseEntity.ok().body(responseDtoList);
    }
}
