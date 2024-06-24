package com.sparta.easyspring.admin.dto;

import com.sparta.easyspring.auth.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class AllOfUserResponseDto {

    private Long id;
    private String username;
    private UserRoleEnum role;

    public AllOfUserResponseDto(Long id, String username, UserRoleEnum role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

}
