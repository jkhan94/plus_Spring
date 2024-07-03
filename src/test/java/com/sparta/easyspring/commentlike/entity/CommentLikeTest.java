package com.sparta.easyspring.commentlike.entity;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.config.MockTestDataSetup;
import com.sparta.easyspring.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentLikeTest {

    @Test
    @DisplayName("CommentLike 생성")
    void createCommentLike() {
        // given
        User TEST_USER = MockTestDataSetup.mockTestUserSetup(1L);
        Post TEST_POST = MockTestDataSetup.mockTestPostSetup(1L, TEST_USER);

        // when
        Comment TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(1L, TEST_USER, TEST_POST);

        // then
        assertNotNull(TEST_COMMENT);
        assertEquals(TEST_USER, TEST_COMMENT.getUser());
        assertEquals(TEST_POST, TEST_COMMENT.getPost());
    }
}