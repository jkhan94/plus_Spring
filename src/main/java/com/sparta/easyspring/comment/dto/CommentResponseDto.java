package com.sparta.easyspring.comment.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private Long likes;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto() {

    }

    public CommentResponseDto(Long id, String comment, Long likes, Long userId, Long postId, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.comment = comment;
        this.likes = likes;
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

}
