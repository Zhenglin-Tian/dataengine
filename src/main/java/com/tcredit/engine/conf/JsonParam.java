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
public class JsonParam implements Serializable {

    private String name;

    @JsonProperty("mapping")
    private List<Mapping> mappings = Lists.newArrayList();

    public List<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
