package com.sparta.easyspring.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.easyspring.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

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

    public PostResponseDto(Post post) {
        this.id= post.getId();
        this.title= post.getTitle();
        this.contents= post.getContents();
        this.likes= post.getLikes();
        this.userId=post.getUser().getId();
        this.createdAt= post.getCreatedAt();
        this.modifiedAt= post.getModifiedAt();
    }
}
