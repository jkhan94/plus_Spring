package com.sparta.easyspring.postlike;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.exception.ErrorEnum;
import com.sparta.easyspring.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    PostLikeRepository postLikeRepository;
    // TODO UserRepository, PostRepository 추가

    public PostLikeResponseDto likePost(long userId, long postId) {
        // TODO userID 있는지 -> USER_NOT_FOUND
        User user = new User();

        // TODO postID 있는지 -> POST_NOT_FOUND
        Post post = new Post();

        // TODO 테이블에 userId, postId로 등록되어 있음 -> DUPLICATE_LIKE
        if (postLikeRepository.findByUserAndPost(userId, postId)) {
            throw new CustomException(ErrorEnum.DUPLICATE_LIKE);
        }
        // TODO 테이블에는 없지만 postID의 userID가 좋아요 누른 userID와 동일 -> CANNOT_LIKE_OWN_CONTENT

        // 좋아요 등록
        PostLike postLike = new PostLike(user, post);
        postLikeRepository.save(postLike);

        return new PostLikeResponseDto("게시글 좋아요 완료");
    }

    public PostLikeResponseDto unlikePost(long userId, long postId) {
        // TODO userID 있는지 -> USER_NOT_FOUND
        User user = new User();

        // TODO postID 있는지 -> POST_NOT_FOUND
        Post post = new Post();

        // TODO 테이블에 userId, postId로 등록X -> LIKE_NOT_FOUND
        if (!postLikeRepository.findByUserAndPost(userId, postId)) {
            throw new CustomException(ErrorEnum.LIKE_NOT_FOUND);
        }

        // 좋아요 해제
        PostLike postLike = new PostLike(user, post);
        postLikeRepository.delete(postLike);

        return new PostLikeResponseDto("게시글 좋아요 해제 완료");
    }
}
