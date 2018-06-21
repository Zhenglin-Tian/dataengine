package com.tcredit.engine.conf.handler.httpHandler;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 16:11
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 16:11
 * @updatedRemark:
 * @version:
 */
public class ServiceCallingManager implements Serializable {
    private ServiceCaller serviceCaller;
    private AttemptManager attemptManager;

    public ServiceCaller getServiceCaller() {
        return serviceCaller;
    }

    public void setServiceCaller(ServiceCaller serviceCaller) {
        this.serviceCaller = serviceCaller;
    }

    public AttemptManager getAttemptManager() {
        return attemptManager;
    }

    public void setAttemptManager(AttemptManager attemptManager) {
        this.attemptManager = attemptManager;
    }
}
