package com.sparta.easyspring.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponseDto {
    private Long id;
    private String username;
    private String introduction;
    private int likedPosts;
    private int likedComments;

    public ProfileResponseDto(Long id, String username, String introduction) {
        this.id = id;
        this.username = username;
        this.introduction = introduction;
    }

}
