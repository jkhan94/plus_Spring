package com.sparta.easyspring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorEnum {
    // user
    USER_NOT_FOUND(400, "등록되지 않은 사용자입니다."),

    // post
    POST_NOT_FOUND(400, "등록되지 않은 게시글입니다."),

    // comment
    COMMENT_NOT_FOUND(400, "등록되지 않은 댓글입니다."),

    // like
    LIKE_NOT_FOUND(400, "좋아요가 설정되지 않았습니다."),
    USER_NOT_AUTHENTICATED(400, "인증되지 않은 사용자입니다. 로그인해주세요."),
    CANNOT_LIKE_OWN_CONTENT(400, "본인이 작성한 콘텐츠에 좋아요를 남길 수 없습니다."),
    DUPLICATE_LIKE(400, "같은 콘텐츠에는 사용자당 한 번만 좋아요가 가능합니다.");

    int statusCode;
    String msg; // 출력 메시지
}
