package com.sparta.easyspring.postlike.entity;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.postlike.config.MockTestDataSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostLikeTest {
    PostLike postLike;

    @Test
    @DisplayName("PostLike 생성자 확인")
    void constructorTest() {
        // given
        String TEST_USER_NAME = "username";
        String TEST_USER_PASSWORD = "password";
        String TEST_POST_TITLE = "post title";
        String TEST_POST_CONTENTS = "post contents";
        long TEST_POST_LIKES = 1L;

        User TEST_USER = MockTestDataSetup.mockTestUserSetup();
        Post TEST_POST = MockTestDataSetup.mockTestPostSetup(TEST_USER);

        // when
        postLike = new PostLike(TEST_USER, TEST_POST);

        // then
        assertNotNull(postLike);
        assertEquals(TEST_USER_NAME, postLike.getUser().getUsername());
        assertEquals(TEST_USER_PASSWORD, postLike.getUser().getPassword());
        assertEquals(TEST_POST_TITLE, postLike.getPost().getTitle());
        assertEquals(TEST_POST_CONTENTS, postLike.getPost().getContents());
        assertEquals(TEST_POST_LIKES, postLike.getPost().getLikes());
    }

}