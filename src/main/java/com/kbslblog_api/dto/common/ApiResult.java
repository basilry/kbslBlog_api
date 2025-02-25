package com.kbslblog_api.dto.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.kbslblog_api.constant.enums.ApiStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ApiResult {

    private int code = ApiStatus.OK.getCode();
    private ApiStatus status = ApiStatus.OK;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int errorCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
    private Object data;

    public ApiResult(ApiStatus apiStatus) {
        this.code = apiStatus.getCode();
        this.status = apiStatus;
    }
}
