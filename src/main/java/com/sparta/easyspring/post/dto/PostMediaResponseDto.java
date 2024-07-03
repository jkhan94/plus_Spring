package com.sparta.easyspring.post.dto;

import com.sparta.easyspring.post.entity.PostMedia;
import lombok.Getter;

@Getter
public class PostMediaResponseDto {
    private Long id;
    private String imageUrl;
    private String filename;
    private Long postId;
    private Long userId;

    public PostMediaResponseDto(PostMedia postMedia) {
        this.id = postMedia.getId();
        this.postId = postMedia.getPost().getId();
        this.userId = postMedia.getUser().getId();
        this.imageUrl = postMedia.getImageUrl();
        this.filename = postMedia.getFilename();
    }
}
