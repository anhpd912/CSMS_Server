package com.fu.coffeeshop_management.server.exception;

import com.fu.coffeeshop_management.server.dto.APIResponse;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, Object> attr = null;
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        try {
            // get constraint violation from exception
            var constraintViolation = e.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);
            // get min value of constraint
            attr = constraintViolation.getConstraintDescriptor().getAttributes();
            return ResponseEntity.badRequest()
                    .body(APIResponse.builder()
                            .isSuccess(false)
                            .message(message)
                            .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.builder()
                            .isSuccess(false)
                            .message(message)
                            .build());
        }
    }

}
