package com.tcredit.engine.processService.impl;

import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.data_process.hbaseDataProcessUtil.HBaseDataProcessUtil;
import com.tcredit.engine.exception.ParamException;
import com.tcredit.engine.processService.BlackListTreService;
import com.tcredit.engine.response.*;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PathUtil;
import com.tcredit.engine.util.ReadCSVUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class BlackListTreServiceImpl implements BlackListTreService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(BlackListTreServiceImpl.class);
    public static final String TABLE_NAME = "tcredit_black_list_full";
    public static final String FAMILY_NAME = "default_column_family";
    public static final String PATH_URL = "/config/black/balck_list.csv";


    @Override
    public BaseResponse insDataHbase() {
        long startTime = System.currentTimeMillis();
        BaseResponse baseResponse = new BaseResponse();
        LOGGER.info("天创黑名单表插入-----" + TABLE_NAME);
        try {
            String projectDir = PathUtil.fetchParent(PathUtil.fetchProjectRootDir(), 2);
            String fileUrl = projectDir + PATH_URL;
            List<Map<String, Object>> list = ReadCSVUtil.readCSVReMap(fileUrl);
            LOGGER.info("天创黑名单读取成功,长度:" + list.size());

            HBaseDataProcessUtil.insList(list, FAMILY_NAME, TABLE_NAME);

            long endTime = System.currentTimeMillis();
            LOGGER.info("天创黑名单插入完成,耗时:" + (endTime - startTime) + "ms");
            baseResponse.setMessage("成功，耗时：" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            LOGGER.error("天创黑名单插入异常,异常信息:", e);
            baseResponse.setCode("-1");
            baseResponse.setMessage("失败：" + e.getMessage());
            return baseResponse;
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<List<Map<String, Object>>> tcreditBlack(HttpServletRequest request) {
        BaseResponse<List<Map<String, Object>>> baseResponse = new BaseResponse<>();
        long startTime = System.currentTimeMillis();
        //入参
        Map<String, Object> params = null;
        String idcard = null;
        String mobile = null;
        List<Map<String, Object>> mList = new ArrayList<>();
        String rowKey = null;
        try {
            params = getParamFromRequest(request);
            boolean chkParam = chkParam(params);
            if (chkParam) {
                idcard = String.valueOf(params.get(ReadCSVUtil.IDCARD));
                mobile = String.valueOf(params.get(ReadCSVUtil.MOBILE));

                LOGGER.info("天创黑名单查询开始,参数:" + JsonUtil.toJson(params));

                mList = HBaseDataProcessUtil.selList(idcard, mobile);

                if (mList.size() != 0) {
                    baseResponse.setData(mList);
                } else {
                    baseResponse.setCode("-5");
                    baseResponse.setMessage("无数据");
                }
            }
        } catch (ParamException e) {
            baseResponse.setCode("-1");
            baseResponse.setMessage(e.getMessage());
            LOGGER.info("天创黑名单查询参数错误:" + e.getMessage());
        } catch (Exception e) {
            baseResponse.setCode("-4");
            baseResponse.setMessage(e.getMessage());
            LOGGER.error("天创黑名单查询异常,异常信息:", e);
        } finally {
            long endTime = System.currentTimeMillis();
            LOGGER.info("天创黑名单查询完成,耗时:" + (endTime - startTime) + "ms");
        }

        return baseResponse;
    }

    public Map<String, Object> getParamFromRequest(HttpServletRequest request) throws ParamException {

        Map<String, Object> map;
        try {
            String paramJson = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
            if (StringUtils.isBlank(paramJson)) {
                LOGGER.error("参数格式错误————退出");
                throw new ParamException("参数为空");
            }
            map = JsonUtil.json2Object(paramJson, Map.class);

            return map;
        } catch (Exception e) {
            LOGGER.error("参数格式错误————退出，错误信息：{}", e);
            throw new ParamException("参数解析错误,请检查参数格式");
        }


    }


    private boolean chkParam(Map<String, Object> requestMap) throws ParamException {
        if (requestMap == null) {
            LOGGER.error("参数错误,缺少param");
            throw new ParamException("参数param为空");
        }
        if (requestMap.get(ReadCSVUtil.IDCARD) == null && requestMap.get(ReadCSVUtil.MOBILE) == null) {
            LOGGER.error("参数错误,缺少" + ReadCSVUtil.IDCARD + "或" + ReadCSVUtil.MOBILE);
            throw new ParamException("缺少参数" + ReadCSVUtil.IDCARD + "或" + ReadCSVUtil.MOBILE);
        }


        return true;
    }
}
