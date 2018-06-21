package com.tcredit.engine.conf;

import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 15:23
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 15:23
 * @updatedRemark:
 * @version:
 */
public class DataModule implements Serializable {
    private String id;

    private String status;

    private String description;

    private DataProcessingUnits dataProcessingUnits;

    private Results results;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public DataProcessingUnits getDataProcessingUnits() {
        return dataProcessingUnits;
    }

    public void setDataProcessingUnits(DataProcessingUnits dataProcessingUnits) {
        this.dataProcessingUnits = dataProcessingUnits;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataModule that = (DataModule) o;

        if (!id.equals(that.id)) return false;
        if (!status.equals(that.status)) return false;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
