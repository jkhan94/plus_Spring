package com.sparta.easyspring.post.controller;

import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.post.dto.PostMediaResponseDto;
import com.sparta.easyspring.post.service.PostMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostMediaController {
    private final PostMediaService postMediaService;

    @GetMapping("/{postId}/media")
    public ResponseEntity<List<PostMediaResponseDto>> getAllFiles(@PathVariable(name = "postId") Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(postMediaService.getAllFiles(postId));
    }

    @PostMapping("/{postId}/media")
    public ResponseEntity<?> uploadFiles(@RequestPart("files") List<MultipartFile> files, @PathVariable(name = "postId") Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            List<Map.Entry<String, String>> fileData = files.stream()
                    .map(file -> {
                        try {
                            return postMediaService.uploadFiles(postId, userDetails.getUser(), file);
                        } catch (IOException e) {
                            throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
                        }
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(fileData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{postId}/media/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable Long postId, @PathVariable String fileName, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            postMediaService.deleteFile(postId, userDetails.getUser(), fileName);
            return ResponseEntity.status(HttpStatus.OK).body("파일 삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제 실패 : " + e.getMessage());
        }
    }

}
