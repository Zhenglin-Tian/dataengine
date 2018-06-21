package com.tcredit.engine.controller;

import com.tcredit.engine.processService.CallBackService;
import com.tcredit.engine.response.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-28 15:52
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-28 15:52
 * @updatedRemark:
 * @version:
 */
//@Controller
//@RequestMapping("/dp/v1")
//public class CallBackController {
//    @Resource
//    private CallBackService callBackService;
//
//    @RequestMapping("/callback")
//    @ResponseBody
//    public ResponseData dbHandler(HttpServletRequest request){
//        return callBackService.dealWithDataHandlingStep(request);
//    }
//}
