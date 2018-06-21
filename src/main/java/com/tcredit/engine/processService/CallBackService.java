package com.tcredit.engine.processService;

import com.tcredit.engine.response.ResponseData;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-28 15:53
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-28 15:53
 * @updatedRemark:
 * @version:
 */
public interface CallBackService {
    /**
     *
     * 参数获取
     * @param request
     * @return
     */
    ResponseData dealWithDataHandlingStep(HttpServletRequest request);
}
