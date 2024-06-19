package com.sparta.easyspring.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponseDto addPost(PostRequestDto requestDto) {
        Post post = new Post(requestDto);
        postRepository.save(post);
        return new PostResponseDto(post);
    }

    public PostResponseDto editPost(Long postId, PostRequestDto requestDto) {
        Post post = findPostbyId(postId);
        post.update(requestDto);
        postRepository.save(post);
        return new PostResponseDto(post);
    }

    public void deletePost(Long postId) {
        Post post = findPostbyId(postId);
        postRepository.delete(post);
    }

    public Post findPostbyId(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                ()->new IllegalArgumentException("찾을 수 없는 포스트 입니다.")
        );
        return post;
    }
}
