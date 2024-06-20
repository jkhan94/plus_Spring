package com.sparta.easyspring.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateProfileRequestDto {
    private String username;
    private String introduction;
}
