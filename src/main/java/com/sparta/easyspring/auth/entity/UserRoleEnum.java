package com.sparta.easyspring.auth.entity;

public enum UserRoleEnum {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String role;

    UserRoleEnum(String role){
        this.role = role;
    }

    public String getRole(){
        return this.role;
    }
}
