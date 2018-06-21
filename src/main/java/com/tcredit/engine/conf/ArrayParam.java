package com.tcredit.engine.conf;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 16:18
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 16:18
 * @updatedRemark:
 * @version:
 */
public class ArrayParam implements Serializable {

    private String name;

    @JsonProperty("json")
    private List<Jsons> jsons = Lists.newArrayList();

    public List<Jsons> getJsons() {
        return jsons;
    }

    public void setJsons(List<Jsons> jsons) {
        this.jsons = jsons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
