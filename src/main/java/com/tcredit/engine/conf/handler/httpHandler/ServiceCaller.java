package com.tcredit.engine.conf.handler.httpHandler;

import com.tcredit.engine.conf.ArrayParam;
import com.tcredit.engine.conf.JsonParam;
import com.tcredit.engine.conf.Mapper;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 16:14
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 16:14
 * @updatedRemark:
 * @version:
 */
public class ServiceCaller implements Serializable {
    private String method = "POST";
    private String url;
    private String paramForm;
    private int timeThreshold;
    private String failurePolicy;
    private Mapper mapper;
    private JsonParam jsonParam;
    private ArrayParam arrayParam;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParamForm() {
        return paramForm;
    }

    public void setParamForm(String paramForm) {
        this.paramForm = paramForm;
    }

    public int getTimeThreshold() {
        return timeThreshold;
    }

    public void setTimeThreshold(int timeThreshold) {
        this.timeThreshold = timeThreshold;
    }

    public String getFailurePolicy() {
        return failurePolicy;
    }

    public void setFailurePolicy(String failurePolicy) {
        this.failurePolicy = failurePolicy;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public JsonParam getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(JsonParam jsonParam) {
        this.jsonParam = jsonParam;
    }

    public ArrayParam getArrayParam() {
        return arrayParam;
    }

    public void setArrayParam(ArrayParam arrayParam) {
        this.arrayParam = arrayParam;
    }
}
