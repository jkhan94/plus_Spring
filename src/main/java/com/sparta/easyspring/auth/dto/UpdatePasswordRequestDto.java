package com.sparta.easyspring.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdatePasswordRequestDto {
    private String username;
    private String password;
    private String newpassword;
}
