package com.sparta.easyspring.admin.service;

import com.sparta.easyspring.admin.dto.AllOfCommentResponseDto;
import com.sparta.easyspring.comment.dto.CommentRequestDto;
import com.sparta.easyspring.comment.dto.CommentResponseDto;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.repository.CommentRepository;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.exception.ErrorEnum;
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

    // 특정 댓글 수정
    public CommentResponseDto modifyCommentByAdmin(Long commentId, CommentRequestDto requestDto) {
        Comment comment = findCommentById(commentId);
        comment.editCommentByAdmin(requestDto);
        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    // 특정 댓글 삭제
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = findCommentById(commentId);
        commentRepository.delete(comment);
    }

    private Comment findCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND));

        return comment;
    }

    private AllOfCommentResponseDto entityToDto(Comment comment) {
        // User를 UserResponseDto로 변환하는 로직을 작성
        return new AllOfCommentResponseDto(comment.getUser().getId(), comment.getPost().getId(), comment.getId(), comment.getContents());
    }
}
