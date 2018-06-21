package com.tcredit.engine.conf.writer.response;

import com.tcredit.engine.conf.Mapper;

import java.io.Serializable;

public class WriterWhere implements Serializable {
    private Mapper mapper;
    private String type;
    private String name;

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
