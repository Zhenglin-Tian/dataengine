package com.tcredit.engine.processService;

import com.tcredit.engine.response.BaseResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-02-03 14:29
 * @updatedUser: zl.T
 * @updatedDate: 2018-02-03 14:29
 * @updatedRemark:
 * @version:
 */
public interface AntifraudUpdateVariableService {
    /**
     * 根据信审结果更新反欺诈计数
     *
     *
     * @param request
     * @return
     */
    BaseResponse update(HttpServletRequest request);
    /**
     * 反欺诈计数
     *
     * @param request
     * @return
     */
    BaseResponse count(HttpServletRequest request);
    /**
     * 反欺查询
     *
     * @param request
     * @return
     */
    BaseResponse select(HttpServletRequest request);
}
