package com.tcredit.engine.controller;

import com.tcredit.engine.processService.DataRetrieveService;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.ResponseCodeEnum;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.util.JsonUtil;
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
 * @since: 2017-12-20 10:05
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-20 10:05
 * @updatedRemark:
 * @version:
 */
@Controller
@RequestMapping("/dp/v1")
public class DataRetrieveController {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataRetrieveController.class);

    @Resource(name = "dataRetrieveServiceImpl")
    private DataRetrieveService retrieveService;

    @RequestMapping("/retrieve")
    @ResponseBody
    public BaseResponse<List<Map<String, Object>>> dataRetrieve(HttpServletRequest request) {
        return retrieve(request);
    }


    //反欺诈查询过滤
    @RequestMapping("/retrieveAntifraud")
    @ResponseBody
    public BaseResponse<List<Map<String, Object>>> dataAntifraudRetrieve(HttpServletRequest request) {
        return retrieveService.retrieveAntifraud(request);
    }


    @RequestMapping("/mretrieve")
    @ResponseBody
    public BaseResponse<Map<String, List<Map<String, Object>>>> manyTableDataRetrieve(HttpServletRequest request) {
        return mretrieve(request);
    }

    private BaseResponse<List<Map<String, Object>>> retrieve(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        RetrieveEntity entity = null;
        try {
            entity = retrieveService.paramParse(request);
            LOGGER.info("查询入参是：" + JsonUtil.toJson(entity));
            return retrieveService.dataRetrieve(entity);
        } catch (Exception e) {
            LOGGER.error("数据查询异常，异常信息：", e);
            return new BaseResponse<>(ResponseCodeEnum.innerFail(e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            if (entity != null) {
                LOGGER.info(String.format("数据查询，阶段:%s,库:%s,查询表:%s,查询字段：%s,查询共耗时：%sms", entity.step, entity.db, entity.tblName, entity.columns, endTime - startTime));
            } else {
                LOGGER.info(String.format("数据查询，阶段:%s,库:%s,查询表:%s,查询字段：%s,查询共耗时：%sms", "", "", "", "", endTime - startTime));
            }
        }
    }

    private BaseResponse<Map<String, List<Map<String, Object>>>> mretrieve(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        RetrieveEntity entity = null;
        try {
            entity = retrieveService.paramParse(request);
            LOGGER.info("查询入参是：" + JsonUtil.toJson(entity));
            return retrieveService.manyTableDataRetrieve(entity);
        } catch (Exception e) {
            LOGGER.error("数据查询异常，异常信息：", e);
            return new BaseResponse<>(ResponseCodeEnum.innerFail(e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            if (entity != null) {
                LOGGER.info(String.format("数据查询，阶段:%s,库:%s,查询表:%s,查询字段：%s,查询共耗时：%sms", entity.step, entity.db, entity.tblName, entity.columns, endTime - startTime));
            } else {
                LOGGER.info(String.format("数据查询，阶段:%s,库:%s,查询表:%s,查询字段：%s,查询共耗时：%sms", "", "", "", "", endTime - startTime));
            }
        }
    }


}
