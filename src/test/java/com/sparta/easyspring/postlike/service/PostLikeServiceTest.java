package com.sparta.easyspring.postlike.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.service.UserService;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.post.service.PostService;
import com.sparta.easyspring.config.MockTestDataSetup;
import com.sparta.easyspring.postlike.entity.PostLike;
import com.sparta.easyspring.postlike.repository.PostLikeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @Mock
    PostLikeRepository postLikeRepository;

    @Mock
    UserService userService;

    @Mock
    PostService postService;

    @InjectMocks
    PostLikeService postLikeService;

    private static User TEST_USER;
    private static User ANOTHER_USER;
    private static Post TEST_POST;


    @BeforeEach
    void setUp() {
        TEST_USER = MockTestDataSetup.mockTestUserSetup();
        ANOTHER_USER = MockTestDataSetup.mockAnotherUserSetup();
    }

    @Test
    @Order(1)
    @DisplayName("성공: 게시글 좋아요")
    void likePost() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_POST_ID = 1L;
        String expectedMsg = "게시글 좋아요 완료";

        TEST_POST = MockTestDataSetup.mockTestPostSetup(1L,ANOTHER_USER);
        PostLike TEST_POSTLIKE = new PostLike(TEST_USER, TEST_POST);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(postService.findPostbyId(TEST_POST_ID)).willReturn(TEST_POST);
        given(postLikeRepository.findByUserAndPost(TEST_USER, TEST_POST)).willReturn(null);
        given(postLikeRepository.save(any())).willReturn(null);

        // when
        String resultMsg = postLikeService.likePost(TEST_USER_ID, TEST_POST_ID);

        // then
        assertEquals(resultMsg, expectedMsg);
    }

    @Test
    @Order(2)
    @DisplayName("실패 : 중복된 좋아요")
    void duplicateLikePost() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_POST_ID = 1L;

        TEST_POST = MockTestDataSetup.mockTestPostSetup(1L,TEST_USER);
        PostLike TEST_POSTLIKE = new PostLike(TEST_USER, TEST_POST);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(postService.findPostbyId(TEST_POST_ID)).willReturn(TEST_POST);
        given(postLikeRepository.findByUserAndPost(TEST_USER, TEST_POST)).willReturn(TEST_POSTLIKE);

        // when - then
        assertThrows(CustomException.class, () -> postLikeService.likePost(TEST_USER_ID, TEST_POST_ID));
    }


    @Test
    @Order(3)
    @DisplayName("실패: 본인이 작성한 게시글에 좋아요를 남길 수 없습니다.")
    void cannotLikeOwnPost() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_POST_ID = 1L;

        Post TEST_POST = MockTestDataSetup.mockTestPostSetup(1L,TEST_USER);
        PostLike TEST_POSTLIKE = new PostLike(TEST_USER, TEST_POST);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(postService.findPostbyId(TEST_POST_ID)).willReturn(TEST_POST);
        given(postLikeRepository.findByUserAndPost(TEST_USER, TEST_POST)).willReturn(null);

        // when - then
        assertThrows(CustomException.class, () -> postLikeService.likePost(TEST_USER_ID, TEST_POST_ID));
    }


    @Test
    @Order(4)
    @DisplayName("성공: 게시글 좋아요 해제")
    void unlikePost() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_POST_ID = 1L;
        String expectedMsg = "게시글 좋아요 해제 완료";

        Post TEST_POST = MockTestDataSetup.mockTestPostSetup(1L,TEST_USER);
        PostLike TEST_POSTLIKE = new PostLike(TEST_USER, TEST_POST);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(postService.findPostbyId(TEST_POST_ID)).willReturn(TEST_POST);
        given(postLikeRepository.findByUserAndPost(TEST_USER, TEST_POST)).willReturn(TEST_POSTLIKE);
        doNothing().when(postLikeRepository).delete(TEST_POSTLIKE);

        // when
        String resultMsg = postLikeService.unlikePost(TEST_USER_ID, TEST_POST_ID);

        // then
        assertEquals(resultMsg, expectedMsg);
    }

    @Test
    @Order(5)
    @DisplayName("실패: 설정되지 않은 좋아요")
    void notLikedPost() {
        // given
        long TEST_USER_ID = 1L;
        long ANOTHER_USER_ID = 2L;
        long TEST_POST_ID = 1L;

        Post TEST_POST = MockTestDataSetup.mockTestPostSetup(1L,ANOTHER_USER);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(postService.findPostbyId(TEST_POST_ID)).willReturn(TEST_POST);
        given(postLikeRepository.findByUserAndPost(TEST_USER, TEST_POST)).willReturn(null);

        // when - then
        assertThrows(CustomException.class, () -> postLikeService.unlikePost(TEST_USER_ID, TEST_POST_ID));
    }

}