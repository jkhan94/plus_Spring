package com.sparta.easyspring.follow.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.service.UserService;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.follow.entity.Follow;
import com.sparta.easyspring.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sparta.easyspring.exception.ErrorEnum.*;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    public void addFollow(Long followingId, User user) {
        User followingUser = userService.findUserById(followingId);
        if (followingId.equals(user.getId())) {
            throw new CustomException(INCORRECT_SELF_FOLLOW);
        }
        Follow checkFollow = findFollowById(followingUser.getId(), user);
        if (checkFollow != null) {
            throw new CustomException(ALREADY_FOLLOW);
        }
        Follow follow = new Follow(followingUser, user);
        followRepository.save(follow);
    }

    public void deleteFollow(Long followingId, User user) {
        User followingUser = userService.findUserById(followingId);
        Follow checkFollow = findFollowById(followingUser.getId(), user);
        if (checkFollow == null) {
            throw new CustomException(NON_EXISTENT_ELEMENT);
        }
        followRepository.delete(checkFollow);
    }

    public Follow findFollowById(Long followingId, User user) {
        return followRepository.findByFollowingIdAndUser(followingId, user);
    }
}
