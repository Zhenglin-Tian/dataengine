package com.tcredit.engine.conf;

import com.google.common.collect.Sets;
import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 15:27
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 15:27
 * @updatedRemark:
 * @version:
 */
public class DataProcessingUnit implements Comparable<DataProcessingUnit>, Serializable {
    private String id;
    private String mid;
    private int order;
    private int percent = 100;
    private String status;
    private String description;
    private Set<Step> step = Sets.newLinkedHashSet();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Step> getStep() {
        return step;
    }

    public void setStep(Set<Step> step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataProcessingUnit that = (DataProcessingUnit) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public int compareTo(DataProcessingUnit o) {
        if (o == null) return -1;
        return this.order - o.order;
    }

    public static void main(String[] args) {

    }
}
