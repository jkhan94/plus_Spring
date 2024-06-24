package com.sparta.easyspring.auth.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    User user;

    @Test
    @DisplayName("User 생성자")
    void constructorTest(){
        // given
        String username = "seokjoon";
        String password = "1234";

        // when
        User user = new User(username, password, UserRoleEnum.USER);

        // then
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(UserRoleEnum.USER, user.getUserRole());
    }
}
