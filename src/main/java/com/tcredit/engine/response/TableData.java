package com.tcredit.engine.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-29 14:16
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-29 14:16
 * @updatedRemark:
 * @version:
 */
public class TableData implements Serializable{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dbName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tableName;
    private List<Map<String, Object>> data = Lists.newArrayList();

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
