package com.sparta.easyspring.post;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> addPost(@Valid @RequestBody PostRequestDto requestDto){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.addPost(requestDto));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> editPost(@PathVariable(name = "postId") Long postId,@RequestBody PostRequestDto requestDto){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.editPost(postId,requestDto));
    }
}
