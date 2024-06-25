package com.sparta.easyspring.admin.service;

import com.sparta.easyspring.admin.dto.AllOfUserResponseDto;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.repository.UserRepository;
import com.sparta.easyspring.exception.CustomException;
import com.sparta.easyspring.exception.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserManagementService {

    private final UserRepository userRepository;

    public void changeUserRole(Long userId, UserRoleEnum userRoleEnum) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorEnum.USER_NOT_FOUND));

        user.setUserRole(userRoleEnum);
        userRepository.save(user);
    }

    public List<AllOfUserResponseDto> getAllUser() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void withdrawUserByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorEnum.USER_NOT_FOUND));

        user.withdraw();
        userRepository.save(user);
    }

    private AllOfUserResponseDto entityToDto(User user) {
        // User를 UserResponseDto로 변환하는 로직을 작성
        return new AllOfUserResponseDto(user.getId(), user.getUsername(), user.getUserRole());
    }
}
