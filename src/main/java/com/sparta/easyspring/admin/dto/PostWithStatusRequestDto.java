package com.sparta.easyspring.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostWithStatusRequestDto {
    @NotBlank(message = "포스트의 제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "포스트의 내용을 입력해주세요.")
    private String contents;

    private Boolean noticeOption = false;
    private Boolean pinnedOption = false;
}