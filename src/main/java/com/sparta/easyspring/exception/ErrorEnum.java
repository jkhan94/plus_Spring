package com.sparta.easyspring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorEnum {

    // Token
    INVALID_TOKEN(400, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRATION(400, "만료된 토큰입니다"),

    // User
    INVALID_USERNAME(400, "아이디는 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다."),
    INVALID_PASSWORD(400, "최소 8자 이상, 15자 이하이며 알파벳 대소문자(az, AZ), 숫자(0~9),특수문자로 구성되어야 합니다."),
    PASSWORD_CHANGE_NOT_ALLOWED(400, "3회 내 설정한 비밀번호 변경 불가"),
    INCORRECT_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(400, "등록되지 않은 사용자입니다."),
    INCORRECT_USER(400, "사용자가 동일하지 않습니다."),
    DUPLICATE_USER(400, "이미 등록된 사용자 입니다."),
    WITHDRAW_USER(400, "탈퇴한 회원입니다."),
    BANNED_USER(403, "BAN 처리된 사용자입니다."),

    // Follow
    NOT_FOLLOW_USER(400, "해당 사용자를 팔로우하지 않습니다."),
    INCORRECT_SELF_FOLLOW(400, "본인을 팔로우 할 수 없습니다."),
    ALREADY_FOLLOW(400, "이미 팔로우된 상태입니다."),

    // Post
    POST_NOT_FOUND(400, "등록되지 않은 게시글입니다."),
    NON_EXISTENT_ELEMENT(400, "존재하지 않는 요소입니다."),

    // Post Media
    EXCEED_MAX_COUNT(400, "파일 업로드는 최대 5개까지 허용됩니다."),
    NOT_ALLOW_IMAGE_SIZE(400, "이미지 파일은 최대 10MB까지 업로드 가능합니다"),
    NOT_ALLOW_VIDEO_SIZE(400, "비디오 및 GIF 파일은 최대 200MB까지 업로드 가능합니다."),
    NOT_ALLOW_FORMAT(400, "허용되지 않는 파일 형식입니다."),
    INCORRECT_FILE_NAME(400, "파일 이름이 유효하지 않습니다."),
    INCORRECT_EXTENSION(400, "파일 학장자를 찾을 수 없습니다."),

    COMMENT_NOT_FOUND(400, "등록되지 않은 댓글입니다."),

    LIKE_NOT_FOUND(400, "좋아요가 설정되지 않았습니다."),
    USER_NOT_AUTHENTICATED(401, "인증되지 않은 사용자입니다. 로그인해주세요."),
    CANNOT_LIKE_OWN_CONTENT(400, "본인이 작성한 콘텐츠에 좋아요를 남길 수 없습니다."),
    DUPLICATE_LIKE(400, "같은 콘텐츠에는 사용자당 한 번만 좋아요가 가능합니다.");

    int statusCode;
    String msg;
}
