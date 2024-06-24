package com.sparta.easyspring.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileRequestDto {
    private String username;
    private String introduction;
}
