package com.sparta.easyspring.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AuthRequestDto {
    private String username;
    private String password;
}
