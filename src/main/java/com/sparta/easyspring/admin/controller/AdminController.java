package com.sparta.easyspring.admin.controller;

import com.sparta.easyspring.admin.dto.RoleChangeRequestDto;
import com.sparta.easyspring.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 특정 유저를 ADMIN 계정으로 변경
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> changeUserRole(@PathVariable("userId") Long userId, @RequestBody RoleChangeRequestDto requestDto) {
        adminService.changeUserRole(userId, requestDto.getUserRoleEnum());

        return ResponseEntity.ok("해당 유저의 역할이 변경되었습니다.");
    }
}
