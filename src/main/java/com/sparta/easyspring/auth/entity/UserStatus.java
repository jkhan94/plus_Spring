package com.sparta.easyspring.auth.entity;

public enum UserStatus {
    ACTIVE("active"),
    WITHDRAW("withdraw");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }
}
