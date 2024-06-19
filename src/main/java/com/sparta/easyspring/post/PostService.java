package com.sparta.easyspring.post;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.comment.dto.CommentResponseDto;
import com.sparta.easyspring.comment.service.CommentService;
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
    private final CommentService commentService;

    public PostResponseDto addPost(PostRequestDto requestDto, User user) {
        Post post = new Post(requestDto,user);
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
        PostResponseDto postResponseDto = new PostResponseDto(post);
        List<CommentResponseDto> commentResponseDtoList = commentService.getAllComments(postId);
        if(commentResponseDtoList.isEmpty()){
            postResponseDto.setComments(null);
        } else {
            postResponseDto.setComments(commentResponseDtoList);
        }
        return postResponseDto;
    }

    public PostResponseDto editPost(Long postId, PostRequestDto requestDto, User user) {
        Post post = findPostbyId(postId);
        if(!post.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("사용자 정보가 일치하지 않아 수정이 불가능합니다.");
        }
        post.update(requestDto);
        postRepository.save(post);
        return new PostResponseDto(post);
    }

    public void deletePost(Long postId, User user) {
        Post post = findPostbyId(postId);
        if(!post.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("사용자 정보가 일치하지 않아 삭제가 불가능합니다.");
        }
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
