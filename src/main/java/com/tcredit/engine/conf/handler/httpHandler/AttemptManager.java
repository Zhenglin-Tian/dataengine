package com.tcredit.engine.conf.handler.httpHandler;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 16:14
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 16:14
 * @updatedRemark:
 * @version:
 */
public class AttemptManager implements Serializable {
    /**
     * 尝试次数
     */
    private int times;
    /**
     * 尝试间隔
     */
    private int intervalInMilli;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getIntervalInMilli() {
        return intervalInMilli;
    }

    public void setIntervalInMilli(int intervalInMilli) {
        this.intervalInMilli = intervalInMilli;
    }
}
