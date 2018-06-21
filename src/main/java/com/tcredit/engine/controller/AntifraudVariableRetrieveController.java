package com.tcredit.engine.controller;

import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.processService.DataRetrieveService;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.ResponseCodeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-03 10:23
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-03 10:23
 * @updatedRemark:
 * @version:
 */
//@Controller
//@RequestMapping("/dp/v1")
@Deprecated
public class AntifraudVariableRetrieveController {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(AntifraudVariableRetrieveController.class);

    @Resource(name = "antifraudVariableRetrieveServiceImpl")
    private DataRetrieveService antifraudVariableRetrieveService;

    @RequestMapping("/antifraudRetrieve")
    @ResponseBody
    public BaseResponse<List<Map<String, Object>>> antifraudVariableDataRetrieve(HttpServletRequest request) {
        return antifraudVariableRetrieve(request);
    }

    /**
     * 执行查询
     *
     * @param request
     * @return
     */
    private BaseResponse<List<Map<String, Object>>> antifraudVariableRetrieve(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        RetrieveEntity entity = null;
        try {
            entity = antifraudVariableRetrieveService.paramParse(request);
            return antifraudVariableRetrieveService.dataRetrieve(entity);
        } catch (Exception e) {
            LOGGER.error("反欺诈数据查询异常，异常信息：", e);
            return new BaseResponse<>(ResponseCodeEnum.innerFail(e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            if (entity != null) {
                LOGGER.info(String.format("反欺诈数据查询，查询表：%s,查询字段：%s,查询共耗时：%sms", entity.tblName, entity.columns, endTime - startTime));
            } else {
                LOGGER.info(String.format("反欺诈数据查询异常，查询表：%s,查询字段：%s,查询共耗时：%sms", "", "", endTime - startTime));
            }
        }

    }
}
