package com.tcredit.engine.conf.writer.response;

import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.writer.Writer;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.report.logging.BeeSmellLog;
import com.tcredit.report.logging.ReportLogHelper;

public class ContextLogWriter implements Writer {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(ContextLogWriter.class);
    private WriterWhere writerWhere;

    private String type = "ctx_log";

    @Override
    public void write(ProcessContextV2 cxt, Step step, ResponseData response) {
        String name = writerWhere.getName();

        //上下文写入数据（数据产品）并打印日志
        BeeSmellLog beeLog = new BeeSmellLog();
        beeLog.setGid(String.valueOf(cxt.get(ProcessContextEnum.GID)));
        beeLog.setTime_risk(System.currentTimeMillis() + "");
        if (response != null) {
            cxt.getStepWriteMap().put(name, response);
            LOGGER.info("gid:" + cxt.get(ProcessContextEnum.GID) + ",mid:" + step.getId() + ",write写入上下文成功,key:" + name);

            if (cxt.isExceptionFlag()) {
                beeLog.setServiceType("FAIL");
            } else {
                if (response.getCode().equals("0")) {
                    beeLog.setServiceType("SUCCESS");
                    beeLog.setFloat_livecanal_hit_sum((response.getData().get(0).getData().get(0).get("livecanal_hit_sum") == null ? null : (String.valueOf(response.getData().get(0).getData().get(0).get("livecanal_hit_sum")))));
                    beeLog.setFloat_loancanal_hit_sum(response.getData().get(0).getData().get(0).get("loancanal_hit_sum") == null ? null : (String.valueOf(response.getData().get(0).getData().get(0).get("loancanal_hit_sum"))));
                    beeLog.setFloat_total_hit_sum((response.getData().get(0).getData().get(0).get("total_hit_sum")) == null ? null : String.valueOf(response.getData().get(0).getData().get(0).get("total_hit_sum")));
                } else {
                    beeLog.setServiceType("FAIL");
                }
            }

        } else {

            beeLog.setServiceType("FAIL");
        }

        ReportLogHelper.print(beeLog);

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
