package com.sparta.easyspring.comment.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.comment.dto.CommentRequestDto;
import com.sparta.easyspring.comment.dto.CommentResponseDto;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.repository.CommentRepository;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.exception.ErrorEnum;
import com.sparta.easyspring.post.Post;
import com.sparta.easyspring.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentResponseDto createNewComment(Long postId, CommentRequestDto requestDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        Post post = postService.findPostbyId(postId);

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

    @Transactional
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
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userDetails.getUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("댓글 작성자가 아님");
        }
        return comment;
    }

    private CommentResponseDto entityToDto(Comment comment) {
        return new CommentResponseDto(comment.getId(), comment.getComment(), comment.getLikes(),
                comment.getUser().getId(), comment.getPost().getId(), comment.getCreatedAt(), comment.getModifiedAt());
    }

    public Comment findCommentbyId(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()->new IllegalArgumentException(ErrorEnum.COMMENT_NOT_FOUND.getMsg())
        );
        return comment;
    }

    @Transactional
    public void increaseLikes(Long commentId){
        Comment comment = findCommentbyId(commentId);
        comment.increaseLikes();
    }

    @Transactional
    public void decreaseLikes(Long commentId){
        Comment comment = findCommentbyId(commentId);
        comment.decreaseLikes();
    }
}
