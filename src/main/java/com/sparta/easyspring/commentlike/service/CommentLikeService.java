package com.sparta.easyspring.commentlike.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.comment.entity.Comment;
import com.sparta.easyspring.comment.repository.CommentRepository;
import com.sparta.easyspring.commentlike.repository.CommentLikeRepository;
import com.sparta.easyspring.commentlike.entity.CommentLike;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.exception.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    public ResponseEntity<String> likeComment(Long userId, Long commentId, User loginUser) {
        // 유저, 댓글 있는지 확인
        User user = checkUser(userId,loginUser);
        Comment comment = checkComment(commentId);

        // 좋아요 유무 확인
        if (commentLikeRepository.findByUserAndComment(user, comment).isPresent()) {
            throw new CustomException(ErrorEnum.DUPLICATE_LIKE);
        }

        // 댓글 작성자 확인
        if (comment.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorEnum.CANNOT_LIKE_OWN_CONTENT);
        }

        // 위에 조건 다 통과하면 댓글 좋아요 추가
        CommentLike commentLike = new CommentLike(user,comment);
        commentLikeRepository.save(commentLike);

        return ResponseEntity.ok("댓글 좋아요 완료");
    }

    public ResponseEntity<String> unlikeComment(Long userId, Long commentId, User loginUser) {
        // 유저, 댓글 있는지 확인
        User user = checkUser(userId,loginUser);
        Comment comment = checkComment(commentId);

        // 댓글 좋아요 가져오기
        CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment)
                .orElseThrow(() -> new CustomException(ErrorEnum.LIKE_NOT_FOUND));

        commentLikeRepository.delete(commentLike);
        return ResponseEntity.ok("댓글 좋아요 해제 완료");
    }

    private User checkUser(Long userId, User loginUser) {
        if(!userId.equals(loginUser.getId())){
            throw new CustomException(ErrorEnum.USER_NOT_FOUND);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorEnum.USER_NOT_FOUND));
    }

    private Comment checkComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND));

    }
}
