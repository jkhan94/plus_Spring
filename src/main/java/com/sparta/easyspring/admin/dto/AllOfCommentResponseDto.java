package com.sparta.easyspring.admin.dto;

import lombok.Getter;

@Getter
public class AllOfCommentResponseDto {
    private Long userId;
    private Long postId;
    private Long commentId;
    private String comment;

    public AllOfCommentResponseDto(Long userId, Long postId, Long commentId, String comment) {
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
        this.comment = comment;
    }
}
