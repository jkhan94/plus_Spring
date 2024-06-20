package com.sparta.easyspring.auth.authController;

import com.sparta.easyspring.auth.dto.AuthRequestDto;
import com.sparta.easyspring.auth.dto.RefreshTokenRequestDto;
import com.sparta.easyspring.auth.dto.UpdatePasswordRequestDto;
import com.sparta.easyspring.auth.dto.UpdateProfileRequestDto;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDto requestDto) {
        return userService.login(requestDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.logout(userDetails);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.withdraw(userDetails);
    }

    @PutMapping("/update/password")
    public ResponseEntity<String> updatePassword(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody UpdatePasswordRequestDto requestDto) {
        return userService.updatePassword(requestDto);
    }

    @PutMapping("/update/profile")
    public ResponseEntity<String> updateProfile(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody
    UpdateProfileRequestDto requestDto) {
        return userService.updateProfile(userDetails, requestDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody RefreshTokenRequestDto requestDto) {
        return userService.refresh(requestDto);
    }
}
