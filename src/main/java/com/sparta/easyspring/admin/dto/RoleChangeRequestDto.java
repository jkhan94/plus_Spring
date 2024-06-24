package com.sparta.easyspring.admin.dto;

import com.sparta.easyspring.auth.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class RoleChangeRequestDto {

    private UserRoleEnum userRoleEnum;
}
