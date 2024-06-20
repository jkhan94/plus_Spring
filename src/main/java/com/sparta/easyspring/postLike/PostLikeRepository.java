package com.sparta.easyspring.postlike;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByUserAndPost(User user, Post post);
}
