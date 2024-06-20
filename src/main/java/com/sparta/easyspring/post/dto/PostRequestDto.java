package com.sparta.easyspring.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostRequestDto {
    @NotBlank(message = "포스트의 제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "포스트의 내용을 입력해주세요.")
    private String contents;
}
