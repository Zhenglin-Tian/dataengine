package com.tcredit.engine.processService;


import com.tcredit.engine.response.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


public interface BlackListTreService {

    BaseResponse insDataHbase();


    BaseResponse<List<Map<String, Object>>> tcreditBlack(HttpServletRequest request);

}
