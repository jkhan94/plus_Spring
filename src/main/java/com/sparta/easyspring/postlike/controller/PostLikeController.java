package com.sparta.easyspring.postlike.controller;

import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.postlike.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sparta.easyspring.exception.ErrorEnum.USER_NOT_AUTHENTICATED;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/like/post/{userId}/{postId}")
    public ResponseEntity<String> likePost(@PathVariable long userId, @PathVariable long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userId != userDetails.getUser().getId()) {
            throw new CustomException(USER_NOT_AUTHENTICATED);
        }

        String successMsg = postLikeService.likePost(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(successMsg);
    }

    @DeleteMapping("/unlike/post/{userId}/{postId}")
    public ResponseEntity<String> unlikePost(@PathVariable long userId, @PathVariable long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userId != userDetails.getUser().getId()) {
            throw new CustomException(USER_NOT_AUTHENTICATED);
        }

        String successMsg = postLikeService.unlikePost(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(successMsg);
    }

}
