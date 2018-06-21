package com.tcredit.engine.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tcredit.engine.util.JsonUtil;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-06 15:42
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-06 15:42
 * @updatedRemark:
 * @version:
 */
public class OutResponse extends Response{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code = "0";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message = "SUCCESS";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rid;
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

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
