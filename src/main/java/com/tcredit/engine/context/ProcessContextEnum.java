package com.tcredit.engine.context;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-30 10:30
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-30 10:30
 * @updatedRemark:
 * @version:
 */
public enum ProcessContextEnum {
    REQUEST("req"),
    CRAW_STD("craw_std"),
    CONFIG("conf"),
    RESPONSE("res"),
    CONTEXT("cxt"),
    INIT_INPUT("init"),
    FUNC("func"),
    GID("gid"),
    ERROR("error"),
    MODULE_ID("mid"),
    SEQ_NUM("seq_num"),
    CHILD_MODULE_ID("cmid"),
    UNIT_INFO("unit"),
    RESULT_STEP("rltStep"),
    START_TIME("startTime"),
    END_TIME("endTime"),
    PROCESS_CONF("processConf"),
    SEQ_NO("seqNo"),
    RAW_DATA("raw"),
    STD_DATA("std"),
    TIDY_DATA("tidy"),
    VARCALC_DATA("varcalc"),
    MODEL_DATA("model"),
    RESULT("result");
    public String val;



    ProcessContextEnum(String val) {
        this.val = val;
    }
}
