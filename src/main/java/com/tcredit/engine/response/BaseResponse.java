package com.tcredit.engine.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tcredit.engine.util.JsonUtil;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-29 15:40
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-29 15:40
 * @updatedRemark:
 * @version:
 */
public class BaseResponse<T> extends Response {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code = "0";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message = "SUCCESS";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public BaseResponse(){

    }

    public BaseResponse(ResponseCodeEnum codeEnum){
        this.code = String.valueOf(codeEnum.getCode());
        this.message = codeEnum.getMessage();
    }

    public BaseResponse(ResponseCodeEnum codeEnum, T data) {
        this(codeEnum);
        this.data = data;
    }

    public BaseResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
