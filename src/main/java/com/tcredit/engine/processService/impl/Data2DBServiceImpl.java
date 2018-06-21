package com.tcredit.engine.processService.impl;

import com.tcredit.engine.conf.Step;
import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.constants.StepConstants;
import com.tcredit.engine.data_process.DataProcessor;
import com.tcredit.engine.dbEntity.Data2DBEntity;
import com.tcredit.engine.processService.Data2DBService;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.ResponseCodeEnum;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-04 22:46
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-04 22:46
 * @updatedRemark:
 * @version:
 */
@Service
public class Data2DBServiceImpl implements Data2DBService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(Data2DBServiceImpl.class);
    /**
     * 数据尝试入库次数
     */
    private static final int DATA2DBCOUNT = 3;
    /**
     * 每次尝试间隔时间
     */
    private static final int ATTEMPT_INTERVAL = 100;


    /**
     * 数据入库
     *
     * @param request
     * @return
     */
    @Override
    public ResponseData data2DB(HttpServletRequest request) {
        if (request == null || StringUtils.isBlank(request.getParameter(HttpConstant.HTTP_PARAM_NAME))) {
            return MessageUtil.convertBaseToOut(new BaseResponse<>(ResponseCodeEnum.paramFail("参数错误")));
        }
        String paramVal = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
        Data2DBEntity data2DBEntity = JsonUtil.json2Object(paramVal, Data2DBEntity.class);
        ResponseCodeEnum chkParam = chkParam(data2DBEntity);
        if (chkParam != null) {
            return MessageUtil.convertBaseToOut(new BaseResponse<>(chkParam));
        }
        /**
         * 入库
         */
        ResponseData response = null;
        try {
            response = data2DB(data2DBEntity.gid, data2DBEntity.rid, data2DBEntity.mid, data2DBEntity.step, data2DBEntity.dbName, data2DBEntity.tableName, data2DBEntity.data, JsonUtil.pojo2Map(data2DBEntity),null);
        } catch (Exception e) {
            LOGGER.error("gid:{},数据模块:{},入库异常，异常信息:{}", data2DBEntity.gid, data2DBEntity.mid, e);
        }


        return response;

    }

    /**
     * @param gid       数据追踪id，多个系统
     * @param rid       数据报告id
     * @param mid       数据模块id
     * @param step      数据处理步骤
     * @param dbName    数据库名
     * @param tableName 数据表名
     * @param data      数据
     * @param otherData 其他相关数据
     * @return
     */
    @Override
    public ResponseData data2DB(String gid, String rid, String mid, String step, String dbName, String tableName, List<Map<String, Object>> data, Map<String, Object> otherData, Step s) {
        /**
         * 数据入库
         */
        boolean dbSuccess = false;
        int count = 0;
        /**
         * 默认入库3次，当且仅当入库失败才会尝试重新入库
         */
        while (!dbSuccess && count < DATA2DBCOUNT) {
            try {
                dbSuccess = data2DBHandle(gid, rid, step, dbName, tableName, data, otherData,s);
            } catch (Exception e) {
                LOGGER.error("gid:{},入库异常，异常信息:{}", gid, e);
                break;
            }
            if (dbSuccess) {
                break;
            }
            try {
                Thread.sleep(ATTEMPT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }


        if (dbSuccess) {
            return MessageUtil.convertBaseToOut(new BaseResponse<>(ResponseCodeEnum.success("入库成功")));
        } else {
            return MessageUtil.convertBaseToOut(new BaseResponse<>(ResponseCodeEnum.innerFail("数据入库失败")));
        }
    }


    /**
     * 参数检查
     *
     * @param entity
     * @return
     */
    private ResponseCodeEnum chkParam(Data2DBEntity entity) {
        if (entity == null) {
            return ResponseCodeEnum.paramFail("入库内容为空");
        }
        if (StringUtils.isBlank(entity.gid)) {
            return ResponseCodeEnum.paramFail("gid为空");
        }
        if (StringUtils.isBlank(entity.rid)) {
            return ResponseCodeEnum.paramFail("rid为空");
        }
        if (StringUtils.isBlank(entity.mid)) {
            return ResponseCodeEnum.paramFail("mid为空");
        }
        if (StringUtils.isBlank(entity.cmid)) {
            return ResponseCodeEnum.paramFail("cmid为空");
        }
        if (StringUtils.isBlank(entity.step)) {
            return ResponseCodeEnum.paramFail("step为空");
        }
        if (StringUtils.isNotBlank(entity.step)) {
            boolean flag = false;
            for (StepConstants sc : StepConstants.values()) {
                if (entity.step.equalsIgnoreCase(sc.name)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return ResponseCodeEnum.paramFail("参数step异常");
            }
        }
        if (entity.data == null || entity.data.isEmpty()) {
            return ResponseCodeEnum.paramFail("数据为空");
        }
        return null;
    }


    /**
     * 数据入库操作
     *
     * @param gid
     * @param rid
     * @param step
     * @param dbName
     * @param tableName
     * @param data
     * @return
     */
    public boolean data2DBHandle(String gid, String rid, String step, String dbName, String tableName, List<Map<String, Object>> data, Map<String, Object> otherData,Step s) {
//        if (StringUtils.isBlank(rid)){
//            rid = gid;
//        }
        if (StringUtils.isBlank(gid) || StringUtils.isBlank(rid) || StringUtils.isBlank(step) || StringUtils.isBlank(dbName) || StringUtils.isBlank(tableName) || data == null || data.isEmpty()) {
            throw new RuntimeException("入库缺少关键信息gid,rid,step,dbName,tableName,data");
        }
        try {
            //DataOperationFactory.getDataStorage("mysql").storage(step, data);
            /**
             * 数据入库
             */
            TableData tableData = new TableData();
            tableData.setDbName(dbName);
            tableData.setTableName(tableName);
            tableData.setData(data);
            DataProcessor.storage(gid, rid, step, tableData, otherData,s);
            return true;
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",当前数据处理步骤：" + step + ",数据入库出错，错误信息：", e);
            return false;
        }
    }
}
