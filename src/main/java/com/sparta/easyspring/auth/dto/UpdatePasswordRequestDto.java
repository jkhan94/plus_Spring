package com.sparta.easyspring.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequestDto {
    private String username;
    private String password;
    private String newpassword;
}
