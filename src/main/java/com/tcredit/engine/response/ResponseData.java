package com.tcredit.engine.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.tcredit.engine.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-29 14:03
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-29 14:03
 * @updatedRemark:
 * @version:
 */
public class ResponseData extends Response implements Serializable {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String msg;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cmid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean sync;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String step;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TableData> data = Lists.newArrayList();

    /**
     * other data
     *
     * @return
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uuid = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bid = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String queryTime = null;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCmid() {
        return cmid;
    }

    public void setCmid(String cmid) {
        this.cmid = cmid;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
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

    public List<TableData> getData() {
        return data;
    }

    public void setData(List<TableData> data) {
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(String queryTime) {
        this.queryTime = queryTime;
    }


    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public static void main(String[] args) {
        ResponseData o = new ResponseData();
        o.setCmid("xx");
        System.out.println(o);

    }
}
