package com.sparta.easyspring.post;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPost(@RequestParam(value = "page",defaultValue = "1") int page,
                                                            @RequestParam(value = "sortBy",defaultValue = "createdAt") String sortBy){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.getAllPost(page-1,sortBy));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable(name = "postId") Long postId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.getPost(postId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> editPost(@PathVariable(name = "postId") Long postId,@RequestBody PostRequestDto requestDto){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.editPost(postId,requestDto));
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "postId") Long postId){
        postService.deletePost(postId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("삭제가 완료되었습니다.");
    }
}
