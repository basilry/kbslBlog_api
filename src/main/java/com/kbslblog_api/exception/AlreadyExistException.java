package com.kbslblog_api.exception;

import com.kbslblog_api.constant.enums.ErrorCode;
import com.kbslblog_api.constant.interfaces.CustomExceptionCode;

public class AlreadyExistException extends RuntimeException implements CustomExceptionCode {

    final int errorCode;
    final String errorMessage;

    /**
     * Constructs a new AlreadyExistException with details extracted from the provided error code.
     *
     * <p>This constructor initializes the exception's error code and message based on the values
     * obtained from the specified ErrorCode instance.</p>
     *
     * @param code the ErrorCode instance used to set the error details
     */
    public AlreadyExistException(ErrorCode code) {
        this.errorCode = code.getCode();
        this.errorMessage = code.getMessage();
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code as an integer.
     */
    @Override
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the error message associated with this exception.
     *
     * @return the error message as a string.
     */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
