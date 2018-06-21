package com.tcredit.engine.conf;

import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-02 15:55
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-02 15:55
 * @updatedRemark:
 * @version:
 */
public class Period implements Serializable{
    public static final String GID = "gid";
    public static final String UUID = "uuid";
    public static final String TID = "tid";
    public static final String RID = "rid";
    public static final String REFER_KEY = "refer_key";
    public static final String TIME_INST = "time_inst";
    public static final String TIME_UPD = "time_upd";
    public static final String DATA_SOURCE = "data_source";
    public static final String MODEL_ID="model_id";

    private String dataSource;
    private int periodInSecond = 0;
    private String mainTable;
    private Params params;


    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public int getPeriodInSecond() {
        return periodInSecond;
    }

    public void setPeriodInSecond(int periodInSecond) {
        this.periodInSecond = periodInSecond;
    }

    public String getMainTable() {
        return mainTable;
    }

    public void setMainTable(String mainTable) {
        this.mainTable = mainTable;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}

