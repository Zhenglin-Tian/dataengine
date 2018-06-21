package com.tcredit.engine.controller;

import com.tcredit.engine.processService.BlackListTreService;
import com.tcredit.engine.response.BaseResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/dp/v1")
public class BlackListTreController {

    @Resource
    private BlackListTreService blackListTreService;

    @RequestMapping("/insDataHbase")
    @ResponseBody
    public BaseResponse insDataMongon() {
        return blackListTreService.insDataHbase();
    }

    @RequestMapping("/tcreditBlack")
    @ResponseBody
    public BaseResponse<List<Map<String, Object>>> tcreditBlack(HttpServletRequest request) {
        return blackListTreService.tcreditBlack(request);
    }

}
