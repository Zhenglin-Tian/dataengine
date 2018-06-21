package com.tcredit.engine.processService.impl;

import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.data_process.DataStorage;
import com.tcredit.engine.data_process.hbaseDataProcessUtil.HBaseDataProcessUtil;
import com.tcredit.engine.processService.PeriodHelperService;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.Response;
import com.tcredit.engine.response.ResponseCodeEnum;
import com.tcredit.engine.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-04-02 09:00
 * @updatedUser: zl.T
 * @updatedDate: 2018-04-02 09:00
 * @updatedRemark:
 * @version:
 */
@Service
public class PeriodHelperServiceImpl implements PeriodHelperService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(PeriodHelperServiceImpl.class);

    @Override
    public BaseResponse<Map<String, Object>> dataRetrieve(String refer_key, String tbleName, String dataSource) {
        BaseResponse response = new BaseResponse();
        if (StringUtils.isBlank(refer_key)) {
            response.setCode(String.valueOf(ResponseCodeEnum.PARAM_ERROR));
            response.setMessage("参数错误");
            return response;
        }

        try {
            String queryKey = null;
            if (StringUtils.isNotBlank(dataSource)) {
                queryKey = HBaseDataProcessUtil.generateRowKey("cat—period", refer_key, dataSource);
            }else {
                queryKey = refer_key;
            }
            tbleName = StringUtils.isNotBlank(tbleName) ? tbleName : DataStorage.HBASE_3d_DS_PERIOD_TB;

            Map<String, Object> map = HBaseDataProcessUtil.queryDataByRowKey("cat-period", queryKey, tbleName);
            response.setData(map);
        } catch (IOException e) {
            LOGGER.error("gid:cat-period,查看数据有效期数据错误，异常信息:", e);
            response.setCode(String.valueOf(ResponseCodeEnum.INNER_FAIL));
            response.setMessage("hbase数据查询错误");
            return response;
        }
        response.setCode(String.valueOf(ResponseCodeEnum.OUT_SUCCESS));
        response.setMessage("成功");
        return response;
    }

    @Override
    public Map<String, String> param(HttpServletRequest request) {
        String parameter = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
        if (StringUtils.isBlank(parameter)) {
            return null;
        }
        Map<String, String> param = JsonUtil.json2Object(parameter, Map.class);
        return param;
    }

    @Override
    public Response dealWithRequest(HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        Map<String, String> param = param(request);
        if (param == null || param.isEmpty()) {
            response.setCode(String.valueOf(ResponseCodeEnum.PARAM_ERROR));
            response.setMessage("参数param错误");
            return response;
        }

        response = dataRetrieve(param.get("referKey"),param.get("tblName"), param.get("dataSource"));
        return response;
    }


}
