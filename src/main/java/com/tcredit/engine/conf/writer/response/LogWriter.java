package com.tcredit.engine.conf.writer.response;

import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.writer.Writer;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.util.DateUtil;
import com.tcredit.report.logging.BeeSmellLog;
import com.tcredit.report.logging.ReportLogHelper;

public class LogWriter implements Writer {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(LogWriter.class);
    private WriterWhere writerWhere;
    private String type = "log";

    @Override
    public void write(ProcessContextV2 cxt, Step step, ResponseData response) {
        //判断std是否执行失败
        if (cxt.isExceptionFlag()) {
            BeeSmellLog beeLog = new BeeSmellLog();
            beeLog.setGid(String.valueOf(cxt.get(ProcessContextEnum.GID)));
            beeLog.setTime_risk(DateUtil.formatNowDate2Str(DateUtil.DATE_FORMAT_yMdHmsSSS));
            beeLog.setServiceType("FAIL");
            ReportLogHelper.print(beeLog);
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
