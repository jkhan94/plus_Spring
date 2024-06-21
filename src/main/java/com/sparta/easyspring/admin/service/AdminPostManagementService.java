package com.sparta.easyspring.admin.service;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.exception.ErrorEnum;
import com.sparta.easyspring.post.dto.PostRequestDto;
import com.sparta.easyspring.post.dto.PostResponseDto;
import com.sparta.easyspring.post.entity.Post;
import com.sparta.easyspring.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostManagementService {

    private final PostRepository postRepository;

    @Transactional
    public List<PostResponseDto> getAllPosts(int page, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, 5, sort);
        Page<Post> postPage = postRepository.findAllSorted(pageable);

        return postPage.stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }


}
