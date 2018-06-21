package com.tcredit.engine.controller.crawlercall;

import com.tcredit.engine.context.ProcessContextHolder;
import com.tcredit.engine.processService.crawlercall.CrawlerCallBackService;
import com.tcredit.engine.response.BaseResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/dp/v1")
public class CrawlerCallBackController {

    @Resource
    private CrawlerCallBackService drawlerCallBackService;

    //公积金爬虫回调
    @RequestMapping("/crawlerCallBack/fundb")
    @ResponseBody
    public BaseResponse fundb(HttpServletRequest request) {
        ProcessContextHolder.setDataHandlerSynchronized(true);
        return drawlerCallBackService.callBack(request, "fundb");
    }

    //社保爬虫回调
    @RequestMapping("/crawlerCallBack/insuranceb")
    @ResponseBody
    public BaseResponse insuranceb(HttpServletRequest request) {
        ProcessContextHolder.setDataHandlerSynchronized(true);
        return drawlerCallBackService.callBack(request, "insuranceb");
    }

    //信用卡邮箱账单爬虫回调
    @RequestMapping("/crawlerCallBack/creditcard")
    @ResponseBody
    public BaseResponse creditcard(HttpServletRequest request) {
        ProcessContextHolder.setDataHandlerSynchronized(true);
        return drawlerCallBackService.callBack(request, "creditcardgetdata");
    }

    //网银爬虫回调
    @RequestMapping("/crawlerCallBack/ebank")
    @ResponseBody
    public BaseResponse ebank(HttpServletRequest request) {
        ProcessContextHolder.setDataHandlerSynchronized(true);
        return drawlerCallBackService.ebankCall(request);
    }

    //运营商爬虫回调
    @RequestMapping("/crawlerCallBack/operator")
    @ResponseBody
    public BaseResponse operator(HttpServletRequest request) {
        ProcessContextHolder.setDataHandlerSynchronized(true);
        return drawlerCallBackService.userreportCll(request);
    }
}
