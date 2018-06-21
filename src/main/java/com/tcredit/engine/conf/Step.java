package com.tcredit.engine.conf;

import com.tcredit.engine.conf.handler.Handler;
import com.tcredit.engine.conf.reader.Reader;
import com.tcredit.engine.conf.writer.Writer;
import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 15:31
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 15:31
 * @updatedRemark:
 * @version:
 */
public class Step implements Serializable {
    private String name;
    private String id;
    private String relyon;
    private Period period;
    private String status;
    private String storeType;
    private boolean persistence=true;
    private Reader reader;
    private Handler handler;
    private Writer writer;
    /** 需要将该步骤返回结果中的哪些字段写在作用域中，以map的形式，通过该步骤的id可以从作用域中取回这些字段 **/
    private List<String> field=new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelyon() {
        return relyon;
    }

    public void setRelyon(String relyon) {
        this.relyon = relyon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return Objects.equals(id, step.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
