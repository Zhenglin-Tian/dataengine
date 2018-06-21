package com.tcredit.engine.processService.crawlercall;


import com.tcredit.engine.response.BaseResponse;

import javax.servlet.http.HttpServletRequest;

public interface CrawlerCallBackService {
    BaseResponse callBack(HttpServletRequest request, String mid);

    BaseResponse ebankCall(HttpServletRequest request);

    BaseResponse userreportCll(HttpServletRequest request);
}
