package com.tcredit.engine.controller;

import com.tcredit.engine.processService.Data2DBService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

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
@RequestMapping("/dp/v1")
public class Data2DBController {
    @Resource
    private Data2DBService data2DBService;
//    @RequestMapping("/dbEntity")
//    @ResponseBody
//    public ResponseData dbHandler(HttpServletRequest request){
//        return data2DBService.data2DB(request);
//    }
}
