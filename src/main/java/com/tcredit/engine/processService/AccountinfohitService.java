package com.tcredit.engine.processService;


import com.tcredit.engine.response.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


public interface AccountinfohitService {

    //历史hbase数据导入mongo
    BaseResponse insDataMongo();

    //mongo数据提供通过手机号查询的接口
    BaseResponse<List<Map<String, Object>>> selectAccountinfohit(HttpServletRequest request);

    //mongo数据导入阿里云hbase
    BaseResponse insDataHbase();

    //hbase数据提供通过手机号查询的接口
    BaseResponse<List<Map<String, Object>>> selectAccountinfohitForHbase(HttpServletRequest request);


}
