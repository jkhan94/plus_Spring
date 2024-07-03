package com.sparta.easyspring.auth.repository;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    String USERNAME = "seokjoon";
    String PASSWORD = "1234";

    @Test
    @DisplayName("유저이름으로 찾기")
    void findByUsername() {
        // given
        User user = new User(USERNAME, PASSWORD, UserRoleEnum.USER);
        userRepository.save(user);

        // when
        User findUser = userRepository.findByUsername(USERNAME).orElse(null);

        // then
        assertEquals(USERNAME, findUser.getUsername());
    }
}
