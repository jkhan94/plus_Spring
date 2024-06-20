package com.sparta.easyspring.follow.entity;

import com.sparta.easyspring.timestamp.TimeStamp;
import com.sparta.easyspring.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Table(name = "follow")
@RequiredArgsConstructor
public class Follow extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "following_id",nullable = false)
    private Long followingId;
    @ManyToOne
    @JoinColumn(name = "follower_id",nullable = false)
    private User user;

    public Follow(User followingUser, User user) {
        this.followingId=followingUser.getId();
        this.user=user;
    }
}
