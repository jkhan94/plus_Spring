package com.sparta.easyspring.commentlike.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.service.UserService;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.service.CommentService;
import com.sparta.easyspring.commentlike.entity.CommentLike;
import com.sparta.easyspring.commentlike.repository.CommentLikeRepository;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.exception.ErrorEnum;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.postlike.MockTestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {
    @Mock
    CommentLikeRepository commentLikeRepository;

    @Mock
    CommentService commentService;

    @Mock
    UserService userService;

    @InjectMocks
    CommentLikeService commentLikeService;

    private static User TEST_USER;
    private static User ANOTHER_USER;
    private static Post TEST_POST;
    private static Comment TEST_COMMENT;


    @BeforeEach
    void setUp() {
        TEST_USER = MockTestDataSetup.mockTestUserSetup();
        ANOTHER_USER = MockTestDataSetup.mockAnotherUserSetup();
    }

    @Test
    @DisplayName("성공: 댓글 좋아요")
    void likeComment() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_COMMENT_ID = 1L;
        String expectedMsg = "댓글 좋아요 완료";

        TEST_POST = MockTestDataSetup.mockTestPostSetup(ANOTHER_USER);
        TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(ANOTHER_USER,TEST_POST);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(commentService.findCommentbyId(TEST_COMMENT_ID)).willReturn(TEST_COMMENT);
        given(commentLikeRepository.findByUserAndComment(TEST_USER, TEST_COMMENT)).willReturn(Optional.empty());
        given(commentLikeRepository.save(any())).willReturn(null);

        // when
        String resultMsg = String.valueOf(commentLikeService.likeComment(TEST_USER_ID,TEST_COMMENT_ID,TEST_USER).getBody());

        // then
        assertEquals(resultMsg, expectedMsg);
    }

    @Test
    @DisplayName("실패: 댓글 좋아요 USER_NOT_AUTHENTICATED 예외 발생")
    void likeCommentFailedCheckUser() {
        // given
        long ANOTHER_USER_ID = 2L;  // 다른 유저 ID
        long TEST_COMMENT_ID = 1L;

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> {
            commentLikeService.likeComment(ANOTHER_USER_ID, TEST_COMMENT_ID, TEST_USER); // 다른 유저의 좋아요 요청
        });
        assertEquals(ErrorEnum.USER_NOT_AUTHENTICATED, exception.getStatusEnum());
    }

    @Test
    @DisplayName("실패 : 중복된 좋아요")
    void duplicateCommentLike() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_COMMENT_ID = 1L;

        TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(ANOTHER_USER,TEST_POST);

        CommentLike commentLike = new CommentLike(TEST_USER,TEST_COMMENT);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(commentService.findCommentbyId(TEST_COMMENT_ID)).willReturn(TEST_COMMENT);
        given(commentLikeRepository.findByUserAndComment(TEST_USER, TEST_COMMENT)).willReturn(Optional.of(commentLike));

        // when - then
        assertThrows(CustomException.class, () -> commentLikeService.likeComment(TEST_USER_ID, TEST_COMMENT_ID,TEST_USER));
    }

    @Test
    @DisplayName("실패: 본인이 작성한 게시글에 좋아요를 남길 수 없습니다.")
    void cannotLikeOwnComment() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_COMMENT_ID = 1L;

        TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(TEST_USER,TEST_POST);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(commentService.findCommentbyId(TEST_COMMENT_ID)).willReturn(TEST_COMMENT);
        given(commentLikeRepository.findByUserAndComment(TEST_USER, TEST_COMMENT)).willReturn(Optional.empty());

        // when - then
        assertThrows(CustomException.class, () -> commentLikeService.likeComment(TEST_USER_ID, TEST_COMMENT_ID,TEST_USER));
    }

    @Test
    @DisplayName("성공: 댓글 좋아요 해제")
    void unlikePost() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_COMMENT_ID = 1L;
        String expectedMsg = "댓글 좋아요 해제 완료";

        TEST_POST = MockTestDataSetup.mockTestPostSetup(ANOTHER_USER);
        TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(ANOTHER_USER,TEST_POST);

        CommentLike TEST_COMMENTLIKE = new CommentLike(TEST_USER,TEST_COMMENT);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(commentService.findCommentbyId(TEST_COMMENT_ID)).willReturn(TEST_COMMENT);
        given(commentLikeRepository.findByUserAndComment(TEST_USER, TEST_COMMENT)).willReturn(Optional.of(TEST_COMMENTLIKE));
        doNothing().when(commentLikeRepository).delete(TEST_COMMENTLIKE);

        // when
        String resultMsg = String.valueOf(commentLikeService.unlikeComment(TEST_USER_ID, TEST_COMMENT_ID,TEST_USER).getBody());

        // then
        assertEquals(resultMsg, expectedMsg);
    }

    @Test
    @DisplayName("실패: 댓글 좋아요 해제 USER_NOT_AUTHENTICATED 예외 발생")
    void unlikeCommentFailedCheckUser() {
        // given
        long ANOTHER_USER_ID = 2L;  // 다른 유저 ID
        long TEST_COMMENT_ID = 1L;

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> {
            commentLikeService.unlikeComment(ANOTHER_USER_ID, TEST_COMMENT_ID, TEST_USER); // 다른 유저의 좋아요 요청
        });
        assertEquals(ErrorEnum.USER_NOT_AUTHENTICATED, exception.getStatusEnum());
    }

    @Test
    @DisplayName("실패: 설정되지 않은 좋아요")
    void notLikedPost() {
        // given
        long TEST_USER_ID = 1L;
        long TEST_COMMENT_ID = 1L;

        TEST_POST = MockTestDataSetup.mockTestPostSetup(ANOTHER_USER);
        TEST_COMMENT = MockTestDataSetup.mockTestCommentSetup(ANOTHER_USER,TEST_POST);

        given(userService.findUserById(TEST_USER_ID)).willReturn(TEST_USER);
        given(commentService.findCommentbyId(TEST_COMMENT_ID)).willReturn(TEST_COMMENT);
        given(commentLikeRepository.findByUserAndComment(TEST_USER, TEST_COMMENT)).willReturn(Optional.empty());

        // when - then
        assertThrows(CustomException.class, () -> commentLikeService.unlikeComment(TEST_USER_ID, TEST_COMMENT_ID,TEST_USER));
    }
}