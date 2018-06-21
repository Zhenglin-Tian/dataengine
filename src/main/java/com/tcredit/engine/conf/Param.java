package com.tcredit.engine.conf;

import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-05 14:51
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-05 14:51
 * @updatedRemark:
 * @version:
 */
public class Param implements Serializable{
    private String name;
    private String value;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
