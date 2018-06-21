package com.tcredit.engine.conf.writer.response;

import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.writer.Writer;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.util.DateUtil;
import com.tcredit.report.logging.BeeSmellLog;
import com.tcredit.report.logging.ReportLogHelper;

public class ContextWriter implements Writer {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(ContextWriter.class);
    private WriterWhere writerWhere;
    private String type = "ctx";

    @Override
    public void write(ProcessContextV2 cxt, Step step, ResponseData response) {
        String name = writerWhere.getName();
        //上下文写入数据（数据产品）
        if (response != null) {
            cxt.getStepWriteMap().put(name, response);
            LOGGER.info("gid:" + cxt.get(ProcessContextEnum.GID) + ",mid:" + step.getId() + ",write写入上下文成功,key:" + name);
        }


    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public WriterWhere getWriterWhere() {
        return writerWhere;
    }

    public void setWriterWhere(WriterWhere writerWhere) {
        this.writerWhere = writerWhere;
    }
}
