package com.sparta.easyspring.comment.service;

import com.sparta.easyspring.auth.service.UserService;
import com.sparta.easyspring.comment.dto.CommentRequestDto;
import com.sparta.easyspring.comment.dto.CommentResponseDto;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    public CommentResponseDto createNewComment(Long postId, CommentRequestDto requestDto) {
//        findById = (repository or service or userDetails 에서)
//        User user = userService.findById or userDetails.getUser()
//        findById (repository or service 에서)
//        Post post = postService.findById(postId).orElseThrow(() -> new IllegalArgumentException("포스트가 존재 하지 않음");

        Comment comment = new Comment(user, post, requestDto.getComment());

        commentRepository.save(comment);

        return entityToDto(comment);
    }

    public List<CommentResponseDto> getAllComments(Long postId) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentResponseDtoList.add(entityToDto(comment));
        }
        return commentResponseDtoList;
    }

    public CommentResponseDto updateExistingComment(Long commentId, CommentRequestDto requestDto) {
        Comment existingComment = getAuthorizedComment(commentId);
        existingComment.editComment(requestDto);
        return entityToDto(existingComment);
    }

    public void deleteExistingComment(Long commentId) {
        Comment comment = getAuthorizedComment(commentId);
        commentRepository.delete(comment);
    }

    private Comment getAuthorizedComment(Long commentId) {
        // 코멘트를 찾고, 코멘트에서 userId를 찾고, loginUser의 userId를 비교
        User loginUser = userDetails.getUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 존재 하지 않음"));

        if (!comment.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("댓글 작성자가 아님");
        }
        return comment;
    }

    private CommentResponseDto entityToDto(Comment comment) {
        return new CommentResponseDto(comment.getId(), comment.getComment(), comment.getLikes(),
                comment.getUser().getId(), comment.getPost().getId(), comment.getCreatedAt(), comment.getModifiedAt());
    }
}
