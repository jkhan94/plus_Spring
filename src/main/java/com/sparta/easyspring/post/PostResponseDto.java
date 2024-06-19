package com.sparta.easyspring.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.easyspring.comment.dto.CommentResponseDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {
    private Long id;
    private String title;
    private String contents;
    private Long likes;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> comments;

    public PostResponseDto(Post post) {
        this.id= post.getId();
        this.title= post.getTitle();
        this.contents= post.getContents();
        this.likes= post.getLikes();
        this.userId=post.getUser().getId();
        this.createdAt= post.getCreatedAt();
        this.modifiedAt= post.getModifiedAt();
    }

    public void setComments(List<CommentResponseDto> comments) { this.comments = comments; }
}
