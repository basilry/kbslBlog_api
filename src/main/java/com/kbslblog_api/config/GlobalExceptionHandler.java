package com.kbslblog_api.config;

import com.kbslblog_api.constant.enums.ApiStatus;
import com.kbslblog_api.constant.enums.ErrorCode;
import com.kbslblog_api.constant.interfaces.CustomExceptionCode;
import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.exception.AlreadyExistException;
import com.kbslblog_api.exception.NotFoundException;
import com.kbslblog_api.exception.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<Object> unAuthException() {
        ApiResult result = new ApiResult();

        result.setCode(ApiStatus.UNAUTHORIZED.getCode());
        result.setStatus(ApiStatus.UNAUTHORIZED);
        result.setErrorCode(ErrorCode.AUTHENTICATION_FAILED.getCode());
        result.setErrorMessage(ErrorCode.AUTHENTICATION_FAILED.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    @ExceptionHandler({ NotFoundException.class, AlreadyExistException.class })
    public ResponseEntity<Object> resourceException(CustomExceptionCode e) {
        ApiResult result = new ApiResult();

        result.setCode(ApiStatus.BAD_REQUEST.getCode());
        result.setStatus(ApiStatus.BAD_REQUEST);
        result.setErrorCode(e.getErrorCode());
        result.setErrorMessage(e.getErrorMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ApiResult result = new ApiResult();
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("유효성 검증 오류가 발생했습니다.");
        
        result.setCode(ApiStatus.BAD_REQUEST.getCode());
        result.setStatus(ApiStatus.BAD_REQUEST);
        result.setErrorCode(ErrorCode.EXCEPTION.getCode());
        result.setErrorMessage(errorMessage);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
