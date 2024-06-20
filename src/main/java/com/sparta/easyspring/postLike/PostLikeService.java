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
    public String likePost(long userId, long postId) {
        User user = userService.findUserById(userId);
        Post post = postService.findPostbyId(postId);

        if (postLikeRepository.findByUserAndPost(user, post) != null) {
            throw new CustomException(DUPLICATE_LIKE);
        }
        if (post.getUser().getId() == userId) {
            throw new CustomException(CANNOT_LIKE_OWN_CONTENT);
        }

        PostLike postLike = new PostLike(user, post);
        postLikeRepository.save(postLike);

        postService.increaseLikes(postId);

        return "게시글 좋아요 완료";
    }

    @Transactional
    public String unlikePost(long userId, long postId) {
        User user = userService.findUserById(userId);
        Post post = postService.findPostbyId(postId);

        PostLike postLike = postLikeRepository.findByUserAndPost(user, post);

        if (postLike == null) {
            throw new CustomException(LIKE_NOT_FOUND);
        }

        postLikeRepository.delete(postLike);

        postService.decreaseLikes(postId);

        return "게시글 좋아요 해제 완료";
    }
}
