package com.tcredit.engine.processService.impl;

import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.context.ProcessContext;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextHolder;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.engine.ProcessEngine;
import com.tcredit.engine.processService.DataProcessingService;
import com.tcredit.engine.response.*;
import com.tcredit.engine.util.DateUtil;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.MessageUtil;
import com.tcredit.engine.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-29 11:19
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-29 11:19
 * @updatedRemark:
 * @version:
 */
@Service
public class DataProcessingServiceImpl2 implements DataProcessingService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataProcessingServiceImpl2.class);

    @Override
    public String dataProcess(HttpServletRequest request) {
        String gid = null;
        Map<String, Object> params = null;
        try {
            //获取参数
            LOGGER.info(String.format("请求进入......................................"));

            params = getParamFromRequest(request);
            LOGGER.info("入参是:" + params);
            gid = String.valueOf(params.get(ProcessContextEnum.GID.val));

            LOGGER.info("gid:" + gid + ",完成参数转化并开始校验");
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",数据处理内部错误，错误信息：", e);
            OutResponse response = MessageUtil.convertBase2Out(new BaseResponse<>(ResponseCodeEnum.innerFail("参数错误")));
            return JsonUtil.toJson(response);
        }

        return process(params);

    }



    /**
     * 数据逻辑
     * @param params
     * @return
     */
    public String process(Map<String, Object> params) {
        String gid = String.valueOf(params.get(ProcessContextEnum.GID.val));
        try {
            //参数转化
            ResponseCodeEnum chkParam = chkParam(params);
            if (chkParam != null && chkParam.getCode() == -1) {
                return JsonUtil.toJson(MessageUtil.convertBase2Out(new BaseResponse<>(chkParam)));
            } else {
                LOGGER.info("gid:" + gid + ",参数校验完成，数据处理开始");

                //创建数据处理过程上下文
                ProcessContextV2 context = buildContext(params);

                LOGGER.info("gid:" + gid + ",处理上下文创建成功，引擎开始数据处理");
                String response = ProcessEngine.start(context);


                return response;
            }
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",数据处理内部错误，错误信息：", e);
            OutResponse response = MessageUtil.convertBase2Out(new BaseResponse<>(ResponseCodeEnum.innerFail("系统繁忙，稍后再试")));
            return JsonUtil.toJson(response);
        }
    }

    /**
     * 参数获取
     * @param request
     * @return
     */
    public Map<String, Object> getParamFromRequest(HttpServletRequest request) {

        Map<String, Object> map;
        try {
            String paramJson = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
            if (StringUtils.isBlank(paramJson)) {
                throw new RuntimeException("参数为空");
            }
            map = JsonUtil.json2Object(paramJson, Map.class);

            return map;
        } catch (Exception e) {
            LOGGER.error("参数解析错误，错误信息：{}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     *
     * @param requestMap
     * @return
     */
    private ResponseCodeEnum chkParam(Map<String, Object> requestMap) {
        if (requestMap == null) {
            return ResponseCodeEnum.paramFail("参数为空！");
        }
        if (requestMap.get(HttpConstant.DATA_MODULE_NAME) == null || StringUtils.isBlank(requestMap.get(HttpConstant.DATA_MODULE_NAME).toString())) {
            return ResponseCodeEnum.paramFail("数据产品mid为空");
        }
        if (requestMap.get(HttpConstant.GLOBE_ID) == null || StringUtils.isBlank(requestMap.get(HttpConstant.GLOBE_ID).toString())) {
            return ResponseCodeEnum.paramFail("没有gid");
        }

        return null;
    }

    @Override
    public ProcessContextV2 buildContext(Map<String, Object> request) {
        ProcessContextV2 cxt = new ProcessContextV2();
        cxt.put(ProcessContextEnum.START_TIME, DateUtil.formatDate2StrFromDate(new Date(), ProcessContextHolder.DATETIMEFORM));
        cxt.put(ProcessContextEnum.REQUEST, request);
        cxt.put(ProcessContextEnum.INIT_INPUT, request);
        cxt.put(ProcessContextEnum.GID, request.get(ProcessContextEnum.GID.val));
        cxt.put(ProcessContextEnum.MODULE_ID, request.get(ProcessContextEnum.MODULE_ID.val));
        /**
         * 将配置文件放进上下文
         */
        cxt.put(ProcessContextEnum.CONFIG, PropertiesUtil.getServiceMap());

        //callbackUrl为空则为同步请求，不为空则为异步访问，将标识写入处理过程的上下文中
        /*Object o = request.get(ProcessContextHolder.CALLBACK_URL);
        if (null != o && StringUtils.isNotBlank(o.toString())) {
            cxt.setSyn(false);
        }*/
        cxt.setSyn(ProcessContextHolder.getDataHandleSync());
        return cxt;
    }
}
