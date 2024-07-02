package com.sparta.easyspring.mylikes.controller;

import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.comment.dto.CommentResponseDto;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.mylikes.service.MyLikesService;
import com.sparta.easyspring.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sparta.easyspring.exception.ErrorEnum.USER_NOT_AUTHENTICATED;

@RestController
@RequestMapping("/mylikes")
@RequiredArgsConstructor
public class MyLikesController {

    private final MyLikesService myLikesService;

    @GetMapping("/posts/{userId}")
    public ResponseEntity<List<PostResponseDto>> getAllLikedPosts(@PathVariable long userId,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @RequestParam(value = "page",defaultValue = "1") int page,
                                                                  @RequestParam(value = "sortBy",defaultValue = "createdAt") String sortBy){
        if (userId != userDetails.getUser().getId()) {
            throw new CustomException(USER_NOT_AUTHENTICATED);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(myLikesService.getAllLikedPost(userId, page-1,sortBy));
    }

    @GetMapping("/comments/{userId}")
    public ResponseEntity<List<CommentResponseDto>> getAllLikedComments(@PathVariable long userId,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                        @RequestParam(value = "page",defaultValue = "1") int page,
                                                                        @RequestParam(value = "sortBy",defaultValue = "createdAt") String sortBy){
        if (userId != userDetails.getUser().getId()) {
            throw new CustomException(USER_NOT_AUTHENTICATED);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(myLikesService.getAllLikedComment(userId, page-1,sortBy));
    }


}
