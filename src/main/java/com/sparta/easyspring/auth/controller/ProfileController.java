package com.sparta.easyspring.auth.controller;

import com.sparta.easyspring.auth.dto.ProfileResponseDto;
import com.sparta.easyspring.auth.dto.UpdateProfileRequestDto;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long id) {
        return userService.getProfile(id);
    }

    @GetMapping()
    public ResponseEntity<List<ProfileResponseDto>> getProfiles() {
        return userService.getProfiles();
    }

    @PutMapping("/update")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody
    UpdateProfileRequestDto requestDto) {
        return userService.updateProfile(userDetails, requestDto);
    }


}
