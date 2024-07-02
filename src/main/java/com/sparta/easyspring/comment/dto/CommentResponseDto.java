package com.sparta.easyspring.comment.dto;

import com.sparta.easyspring.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String contents;
    private Long likes;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public CommentResponseDto(Long id, String contents, Long likes, Long userId, Long postId, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.contents = contents;
        this.likes = likes;
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public CommentResponseDto(Comment contents) {
        this.id = contents.getId();
        this.contents = contents.getContents();
        this.likes = contents.getLikes();
        this.userId = contents.getUser().getId();
        this.postId = contents.getPost().getId();
        this.createdAt = contents.getCreatedAt();
        this.modifiedAt = contents.getModifiedAt();
    }

}
