package com.tcredit.engine.conf;

import com.google.common.collect.Sets;
import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 15:25
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 15:25
 * @updatedRemark:
 * @version:
 */
public class DataProcessingUnits  implements Serializable{
    private String strategy;
    private String failurePolicy;
    private Set<DataProcessingUnit> dataProcessingUnit = Sets.newLinkedHashSet();

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getFailurePolicy() {
        return failurePolicy;
    }

    public void setFailurePolicy(String failurePolicy) {
        this.failurePolicy = failurePolicy;
    }

    public Set<DataProcessingUnit> getDataProcessingUnit() {
        return dataProcessingUnit;
    }

    public void setDataProcessingUnit(Set<DataProcessingUnit> dataProcessingUnit) {
        this.dataProcessingUnit = dataProcessingUnit;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
