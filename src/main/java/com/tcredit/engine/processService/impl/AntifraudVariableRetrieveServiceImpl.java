package com.tcredit.engine.processService.impl;

import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.constants.ResponseConstants;
import com.tcredit.engine.data_process.DataProcessor;
import com.tcredit.engine.dbEntity.AntifraudRetrieveEntity;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.processService.DataRetrieveService;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.ResponseCodeEnum;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-03 09:07
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-03 09:07
 * @updatedRemark:
 * @version:
 */
@Deprecated
@Service("antifraudVariableRetrieveServiceImpl")
public class AntifraudVariableRetrieveServiceImpl implements DataRetrieveService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataRetrieveServiceImpl.class);



    @Override
    public RetrieveEntity paramParse(HttpServletRequest request) {
        if (request == null) return null;
        String paramJson = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
        RetrieveEntity entity = JsonUtil.json2Object(paramJson, AntifraudRetrieveEntity.class);
        if (entity != null) {
            if (StringUtils.isBlank(entity.step)) {
                entity.step = "std";
            }
            if (StringUtils.isBlank(entity.db)) {
                entity.db = "std";
            }
            if (StringUtils.isBlank(entity.tblName)) {
                entity.tblName = PropertiesUtil.getString("tbl_antifraud_variable");
            }
            if (entity.columns == null || entity.columns.isEmpty()) {
                entity.columns.add("gid");
                entity.columns.add("bid");
                entity.columns.add("uuid");
                entity.columns.add("nameinvar");
                entity.columns.add("namerevar");
                entity.columns.add("valuerevar");
            }
        }
        if (entity != null && StringUtils.isNotBlank(entity.step) &&
                StringUtils.isNotBlank(entity.db) &&
                StringUtils.isNotBlank(entity.tblName)) {
            return entity;
        } else {
            return null;
        }
    }

    @Override
    public BaseResponse<List<Map<String, Object>>> dataRetrieve(RetrieveEntity entity) {
        if (!AntifraudRetrieveEntity.class.isInstance(entity)) return null;
        ResponseCodeEnum chkParam = chkParam(entity);
        if (chkParam != null) {
            return new BaseResponse<>(chkParam);
        }

        /**
         * 参数校验成功
         */
        return retrieve((AntifraudRetrieveEntity) entity);
    }


    /**
     * 查询数据
     *
     * @param entity
     * @return
     */
    private BaseResponse<List<Map<String, Object>>> retrieve(AntifraudRetrieveEntity entity) {
        List<Map<String, Object>> data = null;
        try {
            data = DataProcessor.antifraudVariableRetrieveByHBase(entity);
            if (data == null || data.isEmpty()){
                return new BaseResponse<>(ResponseCodeEnum.noData("无数据"));
            }
            return new BaseResponse(ResponseConstants.SUCCESS, ResponseConstants.SUCCESS_MESSAGE, data);
        } catch (Exception e) {
            LOGGER.error(String.format("查询，步骤:%s,数据库:%s,表:%s,出错，错误信息:%s", entity.step, entity.db, entity.tblName, e));
            return new BaseResponse<>(ResponseCodeEnum.innerFail(e.getMessage()));
        }
    }


    /**
     * 参数校验
     *
     * @param entity
     * @return
     */
    private ResponseCodeEnum chkParam(RetrieveEntity entity) {
        if (entity == null || StringUtils.isBlank(entity.step) ||
                StringUtils.isBlank(entity.db) ||
                StringUtils.isBlank(entity.tblName)) {
            return ResponseCodeEnum.paramFail("参数错误");
        }
        return null;
    }

    /**
     * 暂时不提供多表查询
     * @param entity
     * @return
     */
    @Override
    public BaseResponse<Map<String, List<Map<String, Object>>>> manyTableDataRetrieve(RetrieveEntity entity) {
        return null;
    }

    @Override
    public BaseResponse<List<Map<String, Object>>> retrieveAntifraud(HttpServletRequest request) {
        return null;
    }
}
