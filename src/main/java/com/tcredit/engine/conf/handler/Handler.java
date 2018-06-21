package com.tcredit.engine.conf.handler;

import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.handler.httpHandler.ServiceCallingManager;
import com.tcredit.engine.context.ProcessContextV2;

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
public interface Handler extends Serializable{

    /**
     * 返回当前数据处理的报告id
     * @param cxt
     * @param step
     * @return
     */
    String handler(ProcessContextV2 cxt,Step step);


    /**
     *
     * @return
     */
    ServiceCallingManager getServiceCallingManager();
}
