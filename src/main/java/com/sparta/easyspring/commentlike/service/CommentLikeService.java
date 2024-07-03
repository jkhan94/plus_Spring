package com.sparta.easyspring.commentlike.service;


import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.service.UserService;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.service.CommentService;
import com.sparta.easyspring.commentlike.entity.CommentLike;
import com.sparta.easyspring.commentlike.repository.CommentLikeRepository;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.exception.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentService commentService;
    private final UserService userService;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public ResponseEntity<String> likeComment(Long userId, Long commentId, User loginUser) {
        // 로그인 유저와 userId 비교
        checkUser(userId, loginUser);

        // 유저, 댓글 있는지 확인
        User user = userService.findUserById(userId);
        Comment comment = commentService.findCommentbyId(commentId);

        // 좋아요 유무 확인
        if (commentLikeRepository.findByUserAndComment(user, comment).isPresent()) {
            throw new CustomException(ErrorEnum.DUPLICATE_LIKE);
        }

        // 댓글 작성자 확인
        if (comment.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorEnum.CANNOT_LIKE_OWN_CONTENT);
        }

        // 위에 조건 다 통과하면 댓글 좋아요 추가
        CommentLike commentLike = new CommentLike(user, comment);
        commentLikeRepository.save(commentLike);
        // 숫자 증가
        commentService.increaseLikes(commentId);

        return ResponseEntity.ok("댓글 좋아요 완료");
    }

    @Transactional
    public ResponseEntity<String> unlikeComment(Long userId, Long commentId, User loginUser) {
        // 로그인 유저와 userId 비교
        checkUser(userId, loginUser);

        // 유저, 댓글 있는지 확인
        User user = userService.findUserById(userId);
        Comment comment = commentService.findCommentbyId(commentId);

        // 댓글 좋아요 가져오기
        CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment)
                .orElseThrow(() -> new CustomException(ErrorEnum.LIKE_NOT_FOUND));

        // 좋아요 해제
        commentLikeRepository.delete(commentLike);
        // 숫자 감소
        commentService.decreaseLikes(commentId);

        return ResponseEntity.ok("댓글 좋아요 해제 완료");
    }

    private void checkUser(Long userId, User loginUser) {
        if (!userId.equals(loginUser.getId())) {
            throw new CustomException(ErrorEnum.USER_NOT_AUTHENTICATED);
        }
    }
}
