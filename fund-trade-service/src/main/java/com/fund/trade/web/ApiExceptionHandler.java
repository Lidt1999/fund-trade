package com.fund.trade.web;

import com.fund.common.api.Result;
import com.fund.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError() == null ? "请求参数不合法"
                : exception.getBindingResult().getFieldError().getField() + " 参数不合法";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.failure(message));
    }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Result<Void>> handleStatus(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(Result.failure(exception.getReason()));
    }
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<Result<Void>> handleBusiness(BusinessException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Result.failure(exception.getMessage()));
    }
}
