package com.sparta.easyspring.commentlike.repository;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.repository.CommentRepository;
import com.sparta.easyspring.commentlike.entity.CommentLike;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.post.repository.PostRepository;
import com.sparta.easyspring.postlike.config.MockTestDataSetup;
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
class CommentLikeRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;
    @Test
    @DisplayName("성공: 포스트, 사용자로 좋아요한 포스트 찾기")
    void findByUserAndComment(){
        // given
        User TEST_USER = MockTestDataSetup.mockTestUserSetup();
        userRepository.save(TEST_USER);

        Post TEST_POST = MockTestDataSetup.mockTestPostSetup(TEST_USER);
        postRepository.save(TEST_POST);

        Comment TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(TEST_USER,TEST_POST);
        commentRepository.save(TEST_COMMENT);

        CommentLike TEST_COMMENTLIKE = new CommentLike(TEST_USER,TEST_COMMENT);
        ReflectionTestUtils.setField(TEST_COMMENTLIKE, "id", 1L);

        CommentLike savedCommentLike=commentLikeRepository.save(TEST_COMMENTLIKE);

        // when
        CommentLike resultCommentLike = commentLikeRepository.findByUserAndComment(TEST_USER, TEST_COMMENT).orElse(null);

        // then
        assertThat(resultCommentLike).isEqualTo(savedCommentLike);
    }

}