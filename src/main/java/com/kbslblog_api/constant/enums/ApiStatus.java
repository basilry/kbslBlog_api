package com.kbslblog_api.constant.enums;

import lombok.Getter;

@Getter
public enum ApiStatus {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500);

    final int code;

    /**
     * Constructs an ApiStatus enum constant with the provided HTTP status code.
     *
     * @param code the HTTP status code for this enum constant
     */
    ApiStatus(int code) {
        this.code = code;
    }
}