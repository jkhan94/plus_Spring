package com.sparta.easyspring.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 사용 예시
if (예외 발생 조건) {
    throw new CustomException(사용할 ErrorEnum 이름); // throw new CustomException(DUPLICATE_LIKE);
}
*/
@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorEnum statusEnum;
}