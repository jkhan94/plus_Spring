package com.sparta.easyspring.admin.service;

import com.sparta.easyspring.admin.dto.AllOfCommentResponseDto;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCommentManagementService {
    private final CommentRepository commentRepository;

    // 댓글 전체 조회
    public List<AllOfCommentResponseDto> getAllComments() {
        List<Comment> commentList = commentRepository.findAll();
        return commentList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    private AllOfCommentResponseDto entityToDto(Comment comment) {
        // User를 UserResponseDto로 변환하는 로직을 작성
        return new AllOfCommentResponseDto( comment.getUser().getId(), comment.getPost().getId() ,comment.getId(), comment.getComment());
    }
}
