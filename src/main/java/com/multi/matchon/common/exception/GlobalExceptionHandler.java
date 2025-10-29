package com.multi.matchon.common.exception;


import com.multi.matchon.chat.exception.custom.NotChatParticipantException;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.exception.custom.ApiCustomException;

import com.multi.matchon.common.exception.custom.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

// 예외 핸들러가 Security보다 우선 실행되도록 어노테이션 추가
@RestControllerAdvice(basePackages = {"com.multi.matchon"}) // matchon 패키지 내부에 있는 모든 Controller를 스캔함
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    // DTO 유효성 검증 실패 (ex. @NotNull, @NotBlank 등)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.info("{}",ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    // IllegalArgumentException (서비스에서 수동 throw한 경우)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.info("{}",ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(ApiCustomException.class)
    public ResponseEntity<Map<String, String>> ApiCustomException(ApiCustomException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.info("{}",ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NotChatParticipantException.class)
    public ResponseEntity<Map<String, String>> NotChatParticipantException(NotChatParticipantException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.info("{}",ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    // 그 외 모든 예외 처리 (Optional)
    //@ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "알 수 없는 오류가 발생했습니다.");
        log.info("{}",ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    // 모든 예외 상세 메시지 반환 (개발용)
    //@ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        log.info("{}",ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

}
