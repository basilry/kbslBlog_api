package com.kbslblog_api.exception;

import com.kbslblog_api.constant.enums.ErrorCode;
import com.kbslblog_api.constant.interfaces.CustomExceptionCode;

public class AlreadyExistException extends RuntimeException implements CustomExceptionCode {

    final int errorCode;
    final String errorMessage;

    public AlreadyExistException(ErrorCode code) {
        this.errorCode = code.getCode();
        this.errorMessage = code.getMessage();
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
