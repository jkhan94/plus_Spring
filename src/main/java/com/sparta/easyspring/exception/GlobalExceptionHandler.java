package com.sparta.easyspring.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getStatusEnum().getStatusCode())
                .body(ex.getStatusEnum().getMsg());
    }

    // @Valid 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getBindingResult().getFieldError().getDefaultMessage());
    }

    // 나머지 예외 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
