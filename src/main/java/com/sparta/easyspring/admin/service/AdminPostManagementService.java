package com.sparta.easyspring.admin.service;

import com.sparta.easyspring.admin.dto.PostWithStatusRequestDto;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.security.UserDetailsServiceImpl;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.exception.ErrorEnum;
import com.sparta.easyspring.post.dto.PostRequestDto;
import com.sparta.easyspring.post.dto.PostResponseDto;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostManagementService {

    private final PostRepository postRepository;

    @Transactional
    public List<PostResponseDto> getAllPosts(int page, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, 5, sort);
        Page<Post> postPage = postRepository.findAllSorted(pageable);

        return postPage.stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void makePostStatusIsNotice(Long postId) {
        Post post = findPostById(postId);

        post.makeNoticePost(true);
    }

    @Transactional
    public void makePostStatusIsPinned(Long postId) {
        Post post = findPostById(postId);

        post.makePinPost(true);
    }

    @Transactional
    public PostResponseDto modifiedPostByAdmin(Long postId, PostRequestDto requestDto) {
        Post post = findPostById(postId);

        post.update(requestDto);
        postRepository.save(post);
        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePostByAdmin(Long postId) {
        Post post = findPostById(postId);

        postRepository.delete(post);
    }

    @Transactional
    public PostResponseDto addPostByAdmin(PostWithStatusRequestDto requestDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userDetails.getUser();

        Post post = new Post(requestDto,loginUser,loginUser.getUserRole());

        post.makeNoticePost(requestDto.getNoticeOption());
        post.makePinPost(requestDto.getPinnedOption());

        postRepository.save(post);

        return new PostResponseDto(post);
    }

    private Post findPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));

        return post;
    }
}
