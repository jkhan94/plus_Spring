package com.sparta.easyspring.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponseDto {
    private Long id;
    private String username;
    private String indroduction;
}
