package com.tcredit.engine.processService;

import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.dbEntity.RetrieveEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-20 11:45
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-20 11:45
 * @updatedRemark:
 * @version:
 */
public interface DataRetrieveService {
    /**
     * 参数解析
     *
     * @param request
     * @return
     */
    RetrieveEntity paramParse(HttpServletRequest request);

    /**
     * 单表查询数据
     *
     * @param entity
     * @return
     */
    BaseResponse<List<Map<String, Object>>> dataRetrieve(RetrieveEntity entity);


    /**
     * 多表查询数据，查询条件完全一致
     */
    BaseResponse<Map<String, List<Map<String, Object>>>> manyTableDataRetrieve(RetrieveEntity entity);


    /**
     * 查询反欺诈变量（过滤功能）
     *
     * @param request
     * @return
     */
    BaseResponse<List<Map<String, Object>>> retrieveAntifraud(HttpServletRequest request);

}
