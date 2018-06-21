package com.tcredit.engine.controller;

import com.tcredit.engine.context.ProcessContextHolder;
import com.tcredit.engine.processService.DataProcessingService;
import com.tcredit.engine.response.Response;
import com.tcredit.engine.response.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-29 10:57
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-29 10:57
 * @updatedRemark:
 * @version:
 */
@Controller
@RequestMapping("/dp/v1")
public class DataProcessingController {

    @Resource
    private DataProcessingService processingService;

    @RequestMapping("/handle")
    @ResponseBody
    public String dataHandle(HttpServletRequest request) {
        ProcessContextHolder.setDataHandlerSynchronized(true);
        return handle(request);
    }

    @RequestMapping("asynHandle")
    @ResponseBody
    public String callbackDataHandle(HttpServletRequest request) {
        ProcessContextHolder.setDataHandlerSynchronized(false);
        return handle(request);
    }


    /**
     * 开启数据处理
     *
     * @param request
     * @return
     */
    public String handle(HttpServletRequest request) {
        return processingService.dataProcess(request);
    }

}
