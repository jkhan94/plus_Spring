package com.sparta.easyspring.mylikes.repository;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.repository.CommentRepository;
import com.sparta.easyspring.commentlike.entity.CommentLike;
import com.sparta.easyspring.commentlike.repository.CommentLikeRepository;
import com.sparta.easyspring.config.MockTestDataSetup;
import com.sparta.easyspring.config.TestJpaConfig;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.post.repository.PostRepository;
import com.sparta.easyspring.postlike.entity.PostLike;
import com.sparta.easyspring.postlike.repository.PostLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class MyLikesRepositoryTest {

    @Autowired
    MyLikesRepository myLikesRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    User TEST_USER;
    User LIKE_USER;
    User COMMENT_USER;
    Pageable PAGEABLE1;
    Pageable PAGEABLE2;

    @BeforeEach
    void setUp() {
        TEST_USER = MockTestDataSetup.mockTestUserSetup(1L);
        LIKE_USER = MockTestDataSetup.mockTestUserSetup(2L);
        COMMENT_USER = MockTestDataSetup.mockTestUserSetup(3L);

        userRepository.save(TEST_USER);
        userRepository.save(LIKE_USER);
        userRepository.save(COMMENT_USER);

        PAGEABLE1 = MockTestDataSetup.mockPageSetup(0, 5, Sort.by("createdAt").descending());
        PAGEABLE2 = MockTestDataSetup.mockPageSetup(1, 5, Sort.by("createdAt").descending());
    }

    @Test
    @DisplayName("성공 : 내가 좋아하는 게시글 목록 조회")
    void findAllLikedPosts() {
        // given
        List<Post> PAGE_TEST_POST1 = new ArrayList<>();
        List<Post> PAGE_TEST_POST2 = new ArrayList<>();
        List<Post> PAGE_TEST_POST_ALL = new ArrayList<>();
        int postNum = 7;

        for (long i = 1; i <= postNum; i++) {
            Post TEST_POST = MockTestDataSetup.mockTestPostSetup(i, TEST_USER);
            postRepository.save(TEST_POST);
            PostLike TEST_POSTLIKE = new PostLike(LIKE_USER, TEST_POST);
            postLikeRepository.save(TEST_POSTLIKE);
            PAGE_TEST_POST_ALL.add(TEST_POST);
        }

        for (int i = postNum - 1; i >= 0; i--) {
            if (i >= postNum - PAGEABLE1.getPageSize()) {
                PAGE_TEST_POST1.add(PAGE_TEST_POST_ALL.get(i));
            } else {
                PAGE_TEST_POST2.add(PAGE_TEST_POST_ALL.get(i));
            }
        }

        // when
        Page<Post> result1 = myLikesRepository.findAllLikedPosts(LIKE_USER.getId(), PAGEABLE1);
        Page<Post> result2 = myLikesRepository.findAllLikedPosts(LIKE_USER.getId(), PAGEABLE2);

        // then
        for (int i = 0; i < result1.getContent().size(); i++) {
            assertThat(result1.getContent().get(i).getId()).isEqualTo(PAGE_TEST_POST1.get(i).getId());
            assertThat(result1.getContent().get(i).getTitle()).isEqualTo(PAGE_TEST_POST1.get(i).getTitle());
            assertThat(result1.getContent().get(i).getContents()).isEqualTo(PAGE_TEST_POST1.get(i).getContents());
            assertThat(result1.getContent().get(i).getLikes()).isEqualTo(PAGE_TEST_POST1.get(i).getLikes());
            assertThat(result1.getContent().get(i).getUser().getId()).isEqualTo(PAGE_TEST_POST1.get(i).getUser().getId());
            assertThat(result1.getContent().get(i).getUser().getUsername()).isEqualTo(PAGE_TEST_POST1.get(i).getUser().getUsername());
            assertThat(result1.getContent().get(i).getUser().getPassword()).isEqualTo(PAGE_TEST_POST1.get(i).getUser().getPassword());
            assertThat(result1.getContent().get(i).getUser().getUserRole()).isEqualTo(PAGE_TEST_POST1.get(i).getUser().getUserRole());
            assertThat(result1.getContent().get(i).getUser().getUserStatus()).isEqualTo(PAGE_TEST_POST1.get(i).getUser().getUserStatus());
        }
        for (int i = 0; i < result2.getContent().size(); i++) {
            assertThat(result2.getContent().get(i).getId()).isEqualTo(PAGE_TEST_POST2.get(i).getId());
            assertThat(result2.getContent().get(i).getTitle()).isEqualTo(PAGE_TEST_POST2.get(i).getTitle());
            assertThat(result2.getContent().get(i).getContents()).isEqualTo(PAGE_TEST_POST2.get(i).getContents());
            assertThat(result2.getContent().get(i).getLikes()).isEqualTo(PAGE_TEST_POST2.get(i).getLikes());
            assertThat(result2.getContent().get(i).getUser().getId()).isEqualTo(PAGE_TEST_POST2.get(i).getUser().getId());
            assertThat(result2.getContent().get(i).getUser().getUsername()).isEqualTo(PAGE_TEST_POST2.get(i).getUser().getUsername());
            assertThat(result2.getContent().get(i).getUser().getPassword()).isEqualTo(PAGE_TEST_POST2.get(i).getUser().getPassword());
            assertThat(result2.getContent().get(i).getUser().getUserRole()).isEqualTo(PAGE_TEST_POST2.get(i).getUser().getUserRole());
            assertThat(result2.getContent().get(i).getUser().getUserStatus()).isEqualTo(PAGE_TEST_POST2.get(i).getUser().getUserStatus());
        }

    }

    @Test
    @DisplayName("성공 : 내가 좋아하는 댓글 목록 조회")
    void findAllLikeComments() {
        // given
        List<Comment> PAGE_TEST_COMMENT1 = new ArrayList<>();
        List<Comment> PAGE_TEST_COMMENT2 = new ArrayList<>();
        List<Comment> PAGE_TEST_COMMENT_ALL = new ArrayList<>();
        int commentNum = 7;

        for (long i = 1; i <= commentNum; i++) {
            Post TEST_POST = MockTestDataSetup.mockTestPostSetup(i, TEST_USER);
            postRepository.save(TEST_POST);

            Comment TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(i, COMMENT_USER, TEST_POST);
            commentRepository.save(TEST_COMMENT);

            CommentLike TEST_COMMENTLIKE = new CommentLike(LIKE_USER, TEST_COMMENT);
            commentLikeRepository.save(TEST_COMMENTLIKE);
            PAGE_TEST_COMMENT_ALL.add(TEST_COMMENT);
        }

        for (int i = commentNum - 1; i >= 0; i--) {
            if (i >= commentNum - PAGEABLE1.getPageSize()) {
                PAGE_TEST_COMMENT1.add(PAGE_TEST_COMMENT_ALL.get(i));
            } else {
                PAGE_TEST_COMMENT2.add(PAGE_TEST_COMMENT_ALL.get(i));
            }
        }

        // when
        Page<Comment> result1 = myLikesRepository.findAllLikeComments(LIKE_USER.getId(), PAGEABLE1);
        Page<Comment> result2 = myLikesRepository.findAllLikeComments(LIKE_USER.getId(), PAGEABLE2);

        // then
        for (int i = 0; i < result1.getContent().size(); i++) {
            assertThat(result1.getContent().get(i).getId()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getId());
            assertThat(result1.getContent().get(i).getContents()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getContents());
            assertThat(result1.getContent().get(i).getLikes()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getLikes());

            assertThat(result1.getContent().get(i).getPost().getId()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getPost().getId());
            assertThat(result1.getContent().get(i).getPost().getTitle()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getPost().getTitle());
            assertThat(result1.getContent().get(i).getPost().getContents()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getPost().getContents());
            assertThat(result1.getContent().get(i).getPost().getLikes()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getPost().getLikes());

            assertThat(result1.getContent().get(i).getUser().getId()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getUser().getId());
            assertThat(result1.getContent().get(i).getUser().getUsername()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getUser().getUsername());
            assertThat(result1.getContent().get(i).getUser().getPassword()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getUser().getPassword());
            assertThat(result1.getContent().get(i).getUser().getUserRole()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getUser().getUserRole());
            assertThat(result1.getContent().get(i).getUser().getUserStatus()).isEqualTo(PAGE_TEST_COMMENT1.get(i).getUser().getUserStatus());
        }
        for (int i = 0; i < result2.getContent().size(); i++) {
            assertThat(result2.getContent().get(i).getId()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getId());
            assertThat(result2.getContent().get(i).getContents()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getContents());
            assertThat(result2.getContent().get(i).getLikes()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getLikes());

            assertThat(result2.getContent().get(i).getPost().getId()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getPost().getId());
            assertThat(result2.getContent().get(i).getPost().getTitle()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getPost().getTitle());
            assertThat(result2.getContent().get(i).getPost().getContents()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getPost().getContents());
            assertThat(result2.getContent().get(i).getPost().getLikes()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getPost().getLikes());

            assertThat(result2.getContent().get(i).getUser().getId()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getUser().getId());
            assertThat(result1.getContent().get(i).getUser().getUsername()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getUser().getUsername());
            assertThat(result2.getContent().get(i).getUser().getPassword()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getUser().getPassword());
            assertThat(result2.getContent().get(i).getUser().getUserRole()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getUser().getUserRole());
            assertThat(result2.getContent().get(i).getUser().getUserStatus()).isEqualTo(PAGE_TEST_COMMENT2.get(i).getUser().getUserStatus());
        }

    }

    @Test
    @DisplayName("성공 : 내가 좋아요한 게시글 수")
    void countAllLikedPosts() {
        // given
        List<Post> PAGE_TEST_POST_ALL = new ArrayList<>();
        int postNum = 7;

        for (long i = 1; i <= postNum; i++) {
            Post TEST_POST = MockTestDataSetup.mockTestPostSetup(i, TEST_USER);
            postRepository.save(TEST_POST);

            PostLike TEST_POSTLIKE = new PostLike(LIKE_USER, TEST_POST);
            postLikeRepository.save(TEST_POSTLIKE);
            PAGE_TEST_POST_ALL.add(TEST_POST);
        }

        // when
        int result = myLikesRepository.countAllLikedPosts(LIKE_USER.getId());

        // then
        assertThat(result).isEqualTo(PAGE_TEST_POST_ALL.size());
    }

    @Test
    @DisplayName("성공 : 내가 좋아요한 댓글 수")
    void countAllLikedComments() {
        // given
        List<Comment> PAGE_TEST_COMMENT1 = new ArrayList<>();
        List<Comment> PAGE_TEST_COMMENT2 = new ArrayList<>();
        List<Comment> PAGE_TEST_COMMENT_ALL = new ArrayList<>();
        int commentNum = 7;

        for (long i = 1; i <= commentNum; i++) {
            Post TEST_POST = MockTestDataSetup.mockTestPostSetup(i, TEST_USER);
            postRepository.save(TEST_POST);

            Comment TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(i, COMMENT_USER, TEST_POST);
            commentRepository.save(TEST_COMMENT);

            CommentLike TEST_COMMENTLIKE = new CommentLike(LIKE_USER, TEST_COMMENT);
            commentLikeRepository.save(TEST_COMMENTLIKE);
            PAGE_TEST_COMMENT_ALL.add(TEST_COMMENT);
        }

        // when
        int result = myLikesRepository.countAllLikedComments(LIKE_USER.getId());

        // then
        assertThat(result).isEqualTo(PAGE_TEST_COMMENT_ALL.size());
    }
}