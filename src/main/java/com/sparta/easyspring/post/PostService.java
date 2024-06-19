package com.sparta.easyspring.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponseDto addPost(PostRequestDto requestDto) {
        Post post = new Post(requestDto);
        postRepository.save(post);
        return new PostResponseDto(post);
    }

    public List<PostResponseDto> getAllPost(int page, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC,sortBy);
        Pageable pageable = PageRequest.of(page,5,sort);
        Page<PostResponseDto> postPage = postRepository.findAll(pageable).map(PostResponseDto::new);
        List<PostResponseDto> postList = postPage.getContent();
        return postList;
    }

    public PostResponseDto getPost(Long postId) {
        Post post = findPostbyId(postId);
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

    @Transactional
    public void increaseLikes(Long postId){
        Post post = findPostbyId(postId);
        post.increaseLikes();
    }

    @Transactional
    public void decreaseLikes(Long postId){
        Post post = findPostbyId(postId);
        post.decreaseLikes();
    }
}
