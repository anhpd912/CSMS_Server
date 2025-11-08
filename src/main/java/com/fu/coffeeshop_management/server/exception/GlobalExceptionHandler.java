package com.fu.coffeeshop_management.server.exception;

import com.fu.coffeeshop_management.server.dto.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                APIResponse.builder()
                        .isSuccess(false)
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleException(Exception ex) {
        return ResponseEntity.internalServerError().body(
                APIResponse.builder()
                        .isSuccess(false)
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.internalServerError().body(
                APIResponse.builder()
                        .isSuccess(false)
                        .message(ex.getMessage())
                        .build()
        );
    }

}
