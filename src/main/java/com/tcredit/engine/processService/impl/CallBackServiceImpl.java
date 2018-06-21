//package com.tcredit.engine.processService.impl;
//
//import com.tcredit.engine.constants.HttpConstant;
//import com.tcredit.engine.engine.ProcessEngine;
//import com.tcredit.engine.processService.CallBackService;
//import com.tcredit.engine.processService.Data2DBService;
//import com.tcredit.engine.response.BaseResponse;
//import com.tcredit.engine.response.ResponseData;
//import com.tcredit.engine.response.ResponseCodeEnum;
//import com.tcredit.engine.response.TableData;
//import com.tcredit.engine.util.JsonUtil;
//import com.tcredit.engine.util.MessageUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//





//import javax.servlet.http.HttpServletRequest;
//import java.util.Map;
//
///**
// * @description:
// * @author: zl.T
// * @since: 2017-12-28 15:55
// * @updatedUser: zl.T
// * @updatedDate: 2017-12-28 15:55
// * @updatedRemark:
// * @version:
// */
//@Service
//public class CallBackServiceImpl implements CallBackService {
//    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
//            .getLogger(CallBackServiceImpl.class);
//    @Autowired
//    Data2DBService data2DBService;
//
//    @Override
//    public ResponseData dealWithDataHandlingStep(HttpServletRequest request) {
//        if (request == null || StringUtils.isBlank(request.getParameter(HttpConstant.HTTP_PARAM_NAME))) {
//            return MessageUtil.convertBaseToOut(new BaseResponse<>(ResponseCodeEnum.paramFail("参数错误")));
//        }
//        String paramVal = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
//
//        ResponseData response = JsonUtil.json2Object(paramVal, ResponseData.class);
//        if (response.getData() != null) {
//            LOGGER.info(String.format("入库参数,gid:%s,mid:%s,cmid:%s,datas size:%s",response.getGid(),response.getMid(),response.getCmid(),response.getData().size()));
//        }else {
//            LOGGER.info(String.format("入库参数,gid:%s,mid:%s,cmid:%s,datas:%s",response.getGid(),response.getMid(),response.getCmid(),response.getData()));
//        }
//
//
//        ResponseCodeEnum chkParam = ProcessEngine.chkParam(response);
//        if (chkParam != null) {
//            return MessageUtil.convertBaseToOut(new BaseResponse<>(chkParam));
//        }
//        String gid = response.getGid();
//        boolean dbSuccess = true;
//        if (response.getData() != null) {
//            Map<String, Object> map = JsonUtil.pojo2Map(response);
//            map.remove("data");
//            for (TableData tableData : response.getData()) {
//                try {
//                    ResponseData dbRlt = data2DBService.data2DB(gid, response.getMid(), response.getStep(), tableData.getDbName(), tableData.getTableName(), tableData.getData(), map);
//                    if (dbRlt == null || Integer.parseInt(dbRlt.getCode()) != 0) {
//                        throw new RuntimeException("数据入库异常，异常信息：" + dbRlt.getMsg());
//                    }
//                } catch (Exception e) {
//                    LOGGER.error(String.format("gid:%s,步骤:%s,入库异常，异常信息:%s,数据:%s", gid, response.getStep(), e.getMessage(), tableData));
//                    dbSuccess = false;
//                    break;
//                }
//            }
//            if (dbSuccess) {
//                LOGGER.info(String.format("gid:%s,步骤:%s,入库成功", gid, response.getStep()));
//            }
//        }
//
//        ResponseData result = null;
//        if (dbSuccess) {
//            result = ProcessEngine.testNextStep(paramVal);
//        }
//
//        if (result != null) {
//            if (Integer.parseInt(result.getCode()) != 0) {
//                result.setMsg("所有数据入库成功，" + result.getMsg());
//            }
//            return result;
//        } else {
//            return MessageUtil.convertBaseToOut(new BaseResponse<>(ResponseCodeEnum.innerFail("系统繁忙")));
//        }
//    }
//
//
//}
