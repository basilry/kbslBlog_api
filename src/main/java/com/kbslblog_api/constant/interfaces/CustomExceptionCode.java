package com.kbslblog_api.constant.interfaces;

public interface CustomExceptionCode {

    /**
 * Returns the error code representing the specific exception condition.
 *
 * @return an integer error code.
 */
int getErrorCode();

    /**
 * Returns the error message describing the error condition.
 *
 * @return a human-readable message detailing the error
 */
String getErrorMessage();
}
