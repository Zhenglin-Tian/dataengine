package com.tcredit.engine.conf.writer;

import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.writer.response.WriterWhere;
import com.tcredit.engine.context.ProcessContext;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.response.ResponseData;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 15:53
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 15:53
 * @updatedRemark:
 * @version:
 */
public interface Writer extends Serializable {
    void write(ProcessContextV2 cxt, Step step, ResponseData response);

    String getType();

    WriterWhere getWriterWhere();

    void setWriterWhere(WriterWhere writerWhere);

}
