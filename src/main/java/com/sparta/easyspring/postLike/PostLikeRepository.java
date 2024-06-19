package com.sparta.easyspring.postlike;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository {
    boolean findByUserAndPost(long userId, long postId);
}
