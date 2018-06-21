package com.tcredit.engine.conf;

import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result implements Serializable {
    private String stepId;
    private List<String> field = new ArrayList<>();

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public List<String> getField() {
        return field;
    }

    public void setField(List<String> field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
