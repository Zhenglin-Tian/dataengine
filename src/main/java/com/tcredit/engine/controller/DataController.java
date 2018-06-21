package com.tcredit.engine.controller;

import com.tcredit.engine.processService.DataService;
import com.tcredit.engine.response.DataResponse;
import com.tcredit.engine.util.JsonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/dp/v1")
public class DataController {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataController.class);
    @Resource
    private DataService dataService;

    @RequestMapping("/dataProd")
    @ResponseBody
    public DataResponse dataHandle(HttpServletRequest request) {
        DataResponse dataProd = dataService.getDataProd(request);
        LOGGER.info("gid:" + dataProd.getGid() + ",数据产品返回结果:" + JsonUtil.toJson(dataProd));
        return dataProd;
    }


}
