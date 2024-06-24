package com.sparta.easyspring.admin.service;

import com.sparta.easyspring.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCommentManagementService {
    private final CommentRepository commentRepository;
}
