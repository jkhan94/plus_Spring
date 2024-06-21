package com.sparta.easyspring.auth.entity;

import com.sparta.easyspring.auth.dto.AuthRequestDto;
import com.sparta.easyspring.timestamp.TimeStamp;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum userRole;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus;

    @Column
    private String refreshToken;

    @Column
    private String introduction;


    public void updateUsername(String username){
        this.username = username;
    }
    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateIntroduction(String introduction){
        this.introduction = introduction;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void setUserRole(UserRoleEnum userRole) {
        this.userRole = userRole;
    }

    public void withdraw() {
        this.userStatus = UserStatus.WITHDRAW;
        this.refreshToken = null;
    }

    public User(String username, String password, UserRoleEnum userRole) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.userStatus = UserStatus.ACTIVE;
    }

}
