package com.sparta.easyspring.mylikes.service;

import com.sparta.easyspring.comment.dto.CommentResponseDto;
import com.sparta.easyspring.mylikes.repository.MyLikesRepository;
import com.sparta.easyspring.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyLikesService {

    private final MyLikesRepository myLikesRepository;

    public List<PostResponseDto> getAllLikedPost(long userId, int page, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, 5, sort);

        Page<PostResponseDto> postPage = myLikesRepository.findAllLikedPosts(userId, pageable).map(PostResponseDto::new);
        List<PostResponseDto> postList = postPage.getContent();
        return postList;
    }

    public List<CommentResponseDto> getAllLikedComment(long userId, int page, String sortBy) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, sortBy));

        Page<CommentResponseDto> commentPage = myLikesRepository.findAllLikeComments(userId, pageable).map(CommentResponseDto::new);
        List<CommentResponseDto> commentList = commentPage.getContent();
        return commentList;
    }

    public int getCountLikedPosts(Long userId) {
        return myLikesRepository.countAllLikedPosts(userId);
    }

    public int getCountLikedComments(Long userId) {
        return myLikesRepository.countAllLikedComments(userId);
    }
}
