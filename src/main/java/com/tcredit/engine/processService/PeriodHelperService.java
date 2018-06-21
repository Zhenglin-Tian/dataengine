package com.tcredit.engine.processService;

import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-30 18:40
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-30 18:40
 * @updatedRemark:
 * @version:
 */
public interface PeriodHelperService {

    /**
     * @param refer_key
     * @param dataSource
     * @return
     */
    BaseResponse<Map<String, Object>> dataRetrieve(String refer_key, String tbleName, String dataSource);

    /**
     * @param request
     * @return
     */
    Map<String, String> param(HttpServletRequest request);


    /**
     *
     */
    Response dealWithRequest(HttpServletRequest request);
}
