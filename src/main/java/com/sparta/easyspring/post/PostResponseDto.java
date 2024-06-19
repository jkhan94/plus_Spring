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

    public PostResponseDto(Post newPost) {
        this.id= newPost.getId();
        this.title= newPost.getTitle();
        this.contents= newPost.getContents();
        this.likes= newPost.getLikes();
        this.userId= 1L; //newPost.getUser().getId();
        this.createdAt= newPost.getCreatedAt();
        this.modifiedAt= newPost.getModifiedAt();
    }
}
