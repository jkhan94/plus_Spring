package com.sparta.easyspring.postlike;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.service.UserService;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.post.Post;
import com.sparta.easyspring.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.easyspring.exception.ErrorEnum.*;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostService postService;
    private final UserService userService;

    @Transactional
    public PostLikeResponseDto likePost(long userId, long postId) {
        // TODO userID 있는지 -> USER_NOT_FOUND
        User user = userService.findUserById(userId);

        // TODO postID 있는지 -> POST_NOT_FOUND
        Post post = postService.findPostbyId(postId);

        // TODO 테이블에 userId, postId로 등록되어 있음 -> DUPLICATE_LIKE
        if (postLikeRepository.findByUserAndPost(user, post) != null) {
            throw new CustomException(DUPLICATE_LIKE);
        }
        // TODO 테이블에는 없지만 postID의 userID가 좋아요 누른 userID와 동일 -> CANNOT_LIKE_OWN_CONTENT
        if (post.getUser().getId() == userId) {
            throw new CustomException(CANNOT_LIKE_OWN_CONTENT);
        }

        // 좋아요 등록
        PostLike postLike = new PostLike(user, post);
        postLikeRepository.save(postLike);

        postService.increaseLikes(postId);

        return new PostLikeResponseDto("게시글 좋아요 완료");
    }

    @Transactional
    public PostLikeResponseDto unlikePost(long userId, long postId) {
        // TODO userID 있는지 -> USER_NOT_FOUND
        User user = userService.findUserById(userId);

        // TODO postID 있는지 -> POST_NOT_FOUND
        Post post = postService.findPostbyId(postId);

        // TODO 테이블에 userId, postId로 등록X -> LIKE_NOT_FOUND
        if (postLikeRepository.findByUserAndPost(user, post)==null) {
            throw new CustomException(LIKE_NOT_FOUND);
        }

        // 좋아요 해제
        PostLike postLike = new PostLike(user, post);
        postLikeRepository.delete(postLike);

        postService.decreaseLikes(postId);

        return new PostLikeResponseDto("게시글 좋아요 해제 완료");
    }
}
