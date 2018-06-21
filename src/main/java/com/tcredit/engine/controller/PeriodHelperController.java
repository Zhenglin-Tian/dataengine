package com.tcredit.engine.controller;

import com.tcredit.engine.processService.PeriodHelperService;
import com.tcredit.engine.response.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/dp/v1")
public class PeriodHelperController {

    @Resource
    private PeriodHelperService periodHelperService;

    @RequestMapping("/period")
    @ResponseBody
    public Response period(HttpServletRequest request) {
        return periodHelperService.dealWithRequest(request);
    }


}
