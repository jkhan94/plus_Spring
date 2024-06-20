package com.sparta.easyspring.follow.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.service.UserService;
import com.sparta.easyspring.follow.repository.FollowRepository;
import com.sparta.easyspring.follow.entity.Follow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    public void addFollow(Long followingId, User user) {
        User followingUser = userService.findUserById(followingId);
        if(followingId.equals(user.getId())){
            throw new IllegalArgumentException("본인을 팔로우 할 수 없습니다.");
        }
        Follow checkFollow = findFollowById(followingUser.getId(),user);
        if(checkFollow != null){
            throw new IllegalArgumentException("이미 팔로우된 상태입니다.");
        }
        Follow follow = new Follow(followingUser,user);
        followRepository.save(follow);
    }

    public void deleteFollow(Long followingId, User user) {
        User followingUser = userService.findUserById(followingId);
        Follow checkFollow = findFollowById(followingUser.getId(),user);
        if(checkFollow == null){
            throw new IllegalArgumentException("취소할 팔로우가 없습니다.");
        }
        followRepository.delete(checkFollow);
    }

    public Follow findFollowById(Long followingId, User user){
        return followRepository.findByFollowingIdAndUser(followingId, user);
    }
}
