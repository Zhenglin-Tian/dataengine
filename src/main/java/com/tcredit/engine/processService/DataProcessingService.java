package com.tcredit.engine.processService;

import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.response.OutResponse;
import com.tcredit.engine.response.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-29 11:18
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-29 11:18
 * @updatedRemark:
 * @version:
 */
public interface DataProcessingService {
    /**
     * 开始数据处理
     *
     * @param request
     * @return
     */
    String dataProcess(HttpServletRequest request);


    /**
     * 处理
     *
     * @param param
     * @return
     */
    String process(Map<String, Object> param);


    ProcessContextV2 buildContext(Map<String, Object> request);
}
