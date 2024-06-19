package com.sparta.easyspring.post;

import com.mysql.cj.x.protobuf.Mysqlx;
import com.sparta.easyspring.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponseDto addPost(PostRequestDto requestDto) {
        Post newPost = new Post(requestDto);
        postRepository.save(newPost);
        return new PostResponseDto(newPost);
    }

    public PostResponseDto editPost(Long postId, PostRequestDto requestDto) {
        Post editPost = findPostbyId(postId);
        editPost.update(requestDto);
        postRepository.save(editPost);
        return new PostResponseDto(editPost);
    }

    public Post findPostbyId(Long postId){
        Post checkPost = postRepository.findById(postId).orElseThrow(
                ()->new IllegalArgumentException("찾을 수 없는 포스트 입니다.")
        );
        return checkPost;
    }
}
