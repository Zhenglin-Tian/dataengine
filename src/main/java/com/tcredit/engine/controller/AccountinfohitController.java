package com.tcredit.engine.controller;

import com.tcredit.engine.processService.AccountinfohitService;
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
public class AccountinfohitController {

    @Resource
    private AccountinfohitService accountinfohitService;

    @RequestMapping("/insDataMongo")
    @ResponseBody
    public BaseResponse insDataMongo() {
        return accountinfohitService.insDataMongo();
    }

    @RequestMapping("/selectAccountinfohit")
    @ResponseBody
    public BaseResponse<List<Map<String, Object>>> selectAccountinfohit(HttpServletRequest request) {
        return accountinfohitService.selectAccountinfohit(request);
    }

    @RequestMapping("/insMongoDataForHbase")
    @ResponseBody
    public BaseResponse insMongoDataForHbase() {
        return accountinfohitService.insDataHbase();
    }

    @RequestMapping("/queryAccoun")
    @ResponseBody
    public BaseResponse queryAccoun(HttpServletRequest request) {
        return accountinfohitService.selectAccountinfohitForHbase(request);
    }
}
