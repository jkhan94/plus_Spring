package com.sparta.easyspring.postlike.config;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.entity.UserStatus;
import com.sparta.easyspring.post.entity.Post;
import org.springframework.test.util.ReflectionTestUtils;

public class MockTestDataSetup {
    public static User mockTestUserSetup() {
        long TEST_USER_ID = 1L;
        String TEST_USER_NAME = "username";
        String TEST_USER_PASSWORD = "password";
        User TEST_USER = new User();
        ReflectionTestUtils.setField(TEST_USER, "id", TEST_USER_ID);
        ReflectionTestUtils.setField(TEST_USER, "username", TEST_USER_NAME);
        ReflectionTestUtils.setField(TEST_USER, "password", TEST_USER_PASSWORD);
        ReflectionTestUtils.setField(TEST_USER, "userRole", UserRoleEnum.USER);
        ReflectionTestUtils.setField(TEST_USER, "userStatus", UserStatus.ACTIVE);
        return TEST_USER;
    }

    public static User mockAnotherUserSetup() {
        long ANOTHER_USER_ID = 2L;
        String Another_USER_NAME = "username";
        String ANOTHER_USER_PASSWORD = "password";
        User ANOTHER_USER = new User();
        ReflectionTestUtils.setField(ANOTHER_USER, "id", ANOTHER_USER_ID);
        ReflectionTestUtils.setField(ANOTHER_USER, "username", Another_USER_NAME);
        ReflectionTestUtils.setField(ANOTHER_USER, "password", ANOTHER_USER_PASSWORD);
        ReflectionTestUtils.setField(ANOTHER_USER, "userRole", UserRoleEnum.USER);
        ReflectionTestUtils.setField(ANOTHER_USER, "userStatus", UserStatus.ACTIVE);
        return ANOTHER_USER;
    }

    public static Post mockTestPostSetup(User user) {
        long TEST_POST_ID = 1L;
        String TEST_POST_TITLE = "post title";
        String TEST_POST_CONTENTS = "post contents";
        long TEST_POST_LIKES = 1L;
        Post TEST_POST = new Post();
        ReflectionTestUtils.setField(TEST_POST, "id", TEST_POST_ID);
        ReflectionTestUtils.setField(TEST_POST, "title", TEST_POST_TITLE);
        ReflectionTestUtils.setField(TEST_POST, "contents", TEST_POST_CONTENTS);
        ReflectionTestUtils.setField(TEST_POST, "likes", TEST_POST_LIKES);
        ReflectionTestUtils.setField(TEST_POST, "user", user);
        return TEST_POST;
    }
}
