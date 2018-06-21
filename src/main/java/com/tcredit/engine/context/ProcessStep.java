package com.tcredit.engine.context;

import com.tcredit.engine.util.JsonUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-01 14:23
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-01 14:23
 * @updatedRemark:
 * @version:
 */
public class ProcessStep implements Serializable {

    /**
     * 当前正在处理的步骤名称
     */
    private String currentStep;
    /**
     * 当前正在处理步骤的顺序号，主要是针对多个拥有相同步骤名称的情况
     */
    private int order;
    /**
     * 正在处理的状态
     */
    private String processStatus;
    /**
     * 该步骤总共需要请求次数
     */
    private int requstCount = 1;
    /**
     * 标识该次处理是否完成
     */
    private boolean finished = false;

    /**
     * 步骤开始时间
     */
    private Date startTime;

    /**
     * 步骤结束时间
     */
    private Date endTime;

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public int getRequstCount() {
        return requstCount;
    }

    public void setRequstCount(int requstCount) {
        this.requstCount = requstCount;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
