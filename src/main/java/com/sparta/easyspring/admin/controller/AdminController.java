package com.sparta.easyspring.admin.controller;

import com.sparta.easyspring.admin.dto.AllOfUserResponseDto;
import com.sparta.easyspring.admin.dto.PostWithStatusRequestDto;
import com.sparta.easyspring.admin.dto.RoleChangeRequestDto;
import com.sparta.easyspring.admin.service.AdminPostManagementService;
import com.sparta.easyspring.admin.service.AdminUserManagementService;
import com.sparta.easyspring.post.dto.PostRequestDto;
import com.sparta.easyspring.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserManagementService adminUserManagementService;
    private final AdminPostManagementService adminPostManagementService;

    // 특정 유저의 역할을 변경하는 API
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> changeUserRole(@PathVariable("userId") Long userId, @RequestBody RoleChangeRequestDto requestDto) {
        adminUserManagementService.changeUserRole(userId, requestDto.getUserRoleEnum());

        return ResponseEntity.ok("해당 유저의 역할이 변경되었습니다.");
    }
    // 전체 회원 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<AllOfUserResponseDto>> getAllUserByAdmin(){
        List<AllOfUserResponseDto> users = adminUserManagementService.getAllUser();
        return ResponseEntity.ok(users);
    }

    // 특정 회원 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUserByAdmin(@RequestParam("userId") Long userId) {
        adminUserManagementService.deleteUserByAdmin(userId);

        return ResponseEntity.ok("해당 유저의 삭제가 완료되었습니다.");
    }

    // 게시글 전체 목록 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getAllPostsByAdmin(@RequestParam(value = "page") int page,
                                                                    @RequestParam(value = "sortBy") String sortBy) {
        List<PostResponseDto> posts = adminPostManagementService.getAllPosts(page, sortBy);
        return ResponseEntity.ok(posts);
    }

    // 게시글 작성 시 공지와 고정 옵션 선택하도록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/posts/option")
    public ResponseEntity<PostResponseDto> postStatusByAdmin(@RequestBody PostWithStatusRequestDto requestDto) {
        PostResponseDto responseDto = adminPostManagementService.addPostByAdmin(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 게시글 상태 공지로 변경
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/posts/notice/{postId}")
    public ResponseEntity<String> postStatusIsNotice(@PathVariable(value = "postId") Long postId) {
        adminPostManagementService.makePostStatusIsNotice(postId);
        return ResponseEntity.ok("게시글 상태 공지로 변경 완료");
    }

    // 게시글 상태 상단 고정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/posts/pin/{postId}")
    public ResponseEntity<String> postStatusIsPinned(@PathVariable(value = "postId") Long postId) {
        adminPostManagementService.makePostStatusIsPinned(postId);
        return ResponseEntity.ok("게시글 상태 고정으로 변경 완료");
    }

    // 게시글 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostResponseDto> modifiedPostByAdmin(@PathVariable(value = "postId") Long postId, @RequestBody PostRequestDto requestDto) {
        PostResponseDto responseDto = adminPostManagementService.modifiedPostByAdmin(postId, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    // 게시글 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<String> deletePostByAdmin(@PathVariable(value = "postId") Long postId) {
        adminPostManagementService.deletePostByAdmin(postId);

        return ResponseEntity.ok("삭제 되었습니다.");
    }
}
