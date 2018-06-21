package com.tcredit.engine.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Results implements Serializable {
    @JsonProperty("result")
    private List<Result> results = new ArrayList<>();

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
