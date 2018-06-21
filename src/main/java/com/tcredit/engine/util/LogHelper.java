package com.tcredit.engine.util;

import com.tcredit.engine.context.ProcessContext;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextHolder;
import com.tcredit.engine.context.ProcessStep;
import com.tcredit.report.logging.DataHandleLog;
import com.tcredit.report.logging.ReportLogHelper;

import java.util.Date;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-21 19:00
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-21 19:00
 * @updatedRemark:
 * @version:
 */
public class LogHelper {
    /**
     *
     * @param pcxt
     */
    public static void printStep(ProcessContext pcxt) {
        if (pcxt == null) return;
        ProcessStep currentStep = pcxt.getCurrentStep();
        if (currentStep != null) {
            DataHandleLog log = new DataHandleLog();
            Object gid = pcxt.get(ProcessContextEnum.GID);
            if (gid != null) {
                log.setGid(gid.toString());
            }
            Object mid = pcxt.get(ProcessContextEnum.MODULE_ID);
            if (mid != null) {
                log.setMid(mid.toString());
            }
            Object cmid = pcxt.get(ProcessContextEnum.CHILD_MODULE_ID);
            if (cmid != null) {
                log.setCmid(cmid.toString());
            }

            log.setStep(currentStep.getCurrentStep());
            Date startTime = currentStep.getStartTime();
            if (startTime != null) {
                log.setStartTime(startTime);
            } else {
                log.setStartTime(DateUtil.parseString2Date("1976-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
            }
            Date endTime = currentStep.getEndTime();
            if (endTime != null) {
                log.setEndTime(endTime);
            } else {
                log.setEndTime(new Date());
            }

            ReportLogHelper.print(log);
        }
    }

    public static void printOverAll(ProcessContext pcxt) {
        if (pcxt == null) return;

        DataHandleLog log = new DataHandleLog();
        Object gid = pcxt.get(ProcessContextEnum.GID);
        if (gid != null) {
            log.setGid(gid.toString());
        }
        Object mid = pcxt.get(ProcessContextEnum.MODULE_ID);
        if (mid != null) {
            log.setMid(mid.toString());
        }
        Object cmid = pcxt.get(ProcessContextEnum.CHILD_MODULE_ID);
        if (cmid != null) {
            log.setCmid(cmid.toString());
        }

        log.setStep("overall");
        Object startTime = pcxt.get(ProcessContextEnum.START_TIME);

        if (startTime != null) {
            log.setStartTime(DateUtil.parseString2Date(startTime.toString(), ProcessContextHolder.DATETIMEFORM));
        } else {
            log.setStartTime(DateUtil.parseString2Date("1976-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        }
        Object endTime = pcxt.get(ProcessContextEnum.END_TIME);
        if (endTime != null) {
            log.setEndTime(DateUtil.parseString2Date(endTime.toString(),ProcessContextHolder.DATETIMEFORM));
        } else {
            log.setEndTime(new Date());
        }

        ReportLogHelper.print(log);

    }
}
