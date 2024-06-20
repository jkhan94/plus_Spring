package com.sparta.easyspring.follow;

import com.sparta.easyspring.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {

    Follow findByFollowingIdAndUser(Long followingId, User user);
}


