package com.sparta.easyspring.postlike;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<com.sparta.easyspring.postlike.PostLike, Long> {
    com.sparta.easyspring.postlike.PostLike findByUserAndPost(User user, Post post);
}
