package com.sparta.easyspring.postlike;

import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sparta.easyspring.postlike.PostLikeResponseDto;

import static com.sparta.easyspring.exception.ErrorEnum.USER_NOT_AUTHENTICATED;

/*
* 	@PostMapping
	public ResponseEntity<TodoResponseDTO> postTodo(@RequestBody TodoRequestDTO todoRequestDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		TodoResponseDTO responseDTO = todoService.createTodo(todoRequestDTO, userDetails.getUser());
        userDetails.getUser().getId();
		return ResponseEntity.status(201).body(responseDTO);
	}
	* */
@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/like/post/{userId}/{postId}")
    public ResponseEntity<PostLikeResponseDto> likePost(@PathVariable long userId, @PathVariable long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // TODO PathVariable로 받아온 userID = 인증객체 userId 인지 -> USER_NOT_AUTHENTICATED
        if (userId != userDetails.getUser().getId()) {
            throw new CustomException(USER_NOT_AUTHENTICATED);
        }

        PostLikeResponseDto responseDto = postLikeService.likePost(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/unlike/post/{userId}/{postId}")
    public ResponseEntity<PostLikeResponseDto> unlikePost(@PathVariable long userId, @PathVariable long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // TODO PathVariable로 받아온 userID = 인증객체 userId 인지 -> USER_NOT_AUTHENTICATED
        if (userId != userDetails.getUser().getId()) {
            throw new CustomException(USER_NOT_AUTHENTICATED);
        }

        PostLikeResponseDto responseDto = postLikeService.unlikePost(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
