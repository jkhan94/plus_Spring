package com.sparta.easyspring.postlike.repository;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.post.entity.Post;

import com.sparta.easyspring.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByUserAndPost(User user, Post post);
}
