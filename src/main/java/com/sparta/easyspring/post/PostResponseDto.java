package com.sparta.easyspring.post;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
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
        this.userId= 1L; //post.getUser().getId();
        this.createdAt= post.getCreatedAt();
        this.modifiedAt= post.getModifiedAt();
    }
}
