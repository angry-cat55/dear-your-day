package com.example.dearyourdayserver.exception;

import com.example.dearyourdayserver.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // IllegalArgumentException이 발생하면 실행
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        // 1. 에러 메시지 반환 후 저장
        String errorMessage = e.getMessage();

        // 2. JSON 형태로 전환
        ErrorResponse response = new ErrorResponse(errorMessage);

        // 3. 400 Bad Request 상태코드와 함께 전송
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}