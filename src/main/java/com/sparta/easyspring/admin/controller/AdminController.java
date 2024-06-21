package com.sparta.easyspring.admin.controller;

import com.sparta.easyspring.admin.dto.AllOfUserResponseDto;
import com.sparta.easyspring.admin.dto.RoleChangeRequestDto;
import com.sparta.easyspring.admin.service.AdminService;
import com.sparta.easyspring.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 특정 유저의 역할을 변경하는 API
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> changeUserRole(@PathVariable("userId") Long userId, @RequestBody RoleChangeRequestDto requestDto) {
        adminService.changeUserRole(userId, requestDto.getUserRoleEnum());

        return ResponseEntity.ok("해당 유저의 역할이 변경되었습니다.");
    }
    // 전체 회원 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<AllOfUserResponseDto>> getAllUserByAdmin(){
        List<AllOfUserResponseDto> users = adminService.getAllUser();
        return ResponseEntity.ok(users);
    }

    // 특정 회원 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUserByAdmin(@RequestParam("userId") Long userId) {
        adminService.deleteUserByAdmin(userId);

        return ResponseEntity.ok("해당 유저의 삭제가 완료되었습니다.");
    }


    // todo : 특정 회원 정보 수정, 특정 회원 삭제
}
