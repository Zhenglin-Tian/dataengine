package com.tcredit.engine.controller;

import com.tcredit.engine.processService.AntifraudUpdateVariableService;
import com.tcredit.engine.response.BaseResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-04 22:40
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-04 22:40
 * @updatedRemark:
 * @version:
 */
@Controller
@RequestMapping("/dp/v1/antifraud")
public class AntifraudUpdateVariableController {

    @Resource
    private AntifraudUpdateVariableService antifraudUpdateVariableService;


    @RequestMapping("/update")
    @ResponseBody
    public BaseResponse dbHandler(HttpServletRequest request) {
        return antifraudUpdateVariableService.update(request);
    }

    @RequestMapping("/count")
    @ResponseBody
    public BaseResponse countHandler(HttpServletRequest request) {
        return antifraudUpdateVariableService.count(request);
    }

    @RequestMapping("/select")
    @ResponseBody
    public BaseResponse selectHandler(HttpServletRequest request) {
        return antifraudUpdateVariableService.select(request);
    }
}
