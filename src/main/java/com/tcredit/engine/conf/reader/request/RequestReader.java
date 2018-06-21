package com.tcredit.engine.conf.reader.request;

import com.tcredit.engine.conf.Mapper;
import com.tcredit.engine.conf.reader.Reader;
import com.tcredit.engine.context.ProcessContextV2;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 16:48
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 16:48
 * @updatedRemark:
 * @version:
 */
public class RequestReader implements Reader {
    private String type="request";
    private Mapper mapper;

    @Override
    public void read(ProcessContextV2 cxt) {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}
