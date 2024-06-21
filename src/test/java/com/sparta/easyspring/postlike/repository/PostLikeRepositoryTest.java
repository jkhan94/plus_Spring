package com.sparta.easyspring.postlike.repository;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.post.repository.PostRepository;
import com.sparta.easyspring.postlike.entity.PostLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PostLikeRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @Test
    @DisplayName("성공: 포스트, 사용자로 좋아요한 포스트 찾기")
    void findByUserAndPost() {
        // given
        String TEST_USER_NAME = "username";
        String TEST_USER_PASSWORD = "password";
        User TEST_USER = new User(TEST_USER_NAME, TEST_USER_PASSWORD, UserRoleEnum.USER);
        userRepository.save(TEST_USER);

        Post TEST_POST = new Post();
        ReflectionTestUtils.setField(TEST_POST,"title","title");
        ReflectionTestUtils.setField(TEST_POST,"contents","contents");
        ReflectionTestUtils.setField(TEST_POST,"likes",1L);
        ReflectionTestUtils.setField(TEST_POST,"user" ,TEST_USER);
        postRepository.save(TEST_POST);

        PostLike TEST_POSTLIKE = new PostLike(TEST_USER, TEST_POST);
        postLikeRepository.save(TEST_POSTLIKE);

        // when
        PostLike resultPostLike = postLikeRepository.findByUserAndPost(TEST_USER, TEST_POST);

        // then
        assertThat(resultPostLike).isEqualTo(TEST_POSTLIKE);
    }

}