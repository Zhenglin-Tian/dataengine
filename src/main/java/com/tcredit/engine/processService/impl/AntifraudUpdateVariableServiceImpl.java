package com.tcredit.engine.processService.impl;

import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.processService.AntifraudUpdateVariableService;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.ResponseCodeEnum;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PropertiesUtil;
import com.tcredit.engine.util.httpClient.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-02-03 14:31
 * @updatedUser: zl.T
 * @updatedDate: 2018-02-03 14:31
 * @updatedRemark:
 * @version:
 */
@Service
public class AntifraudUpdateVariableServiceImpl implements AntifraudUpdateVariableService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(AntifraudUpdateVariableServiceImpl.class);


    @Resource
    private Data2DBServiceImpl data2DBServiceImpl;

    @Override
    public BaseResponse update(HttpServletRequest request) {
        LOGGER.info(String.format("进入反欺诈计数更新"));
        String gid = null;
        BaseResponse response = new BaseResponse();
        Map<String, String> params = null;
        ResponseCodeEnum chkParam = null;
        try {
            //参数校验gid,status;
            params = getParamFromRequest(request);
            gid = String.valueOf(params.get(ProcessContextEnum.GID.val));
            LOGGER.info(String.format("参数格式验证通过：%s , gid:%s", params, gid));
        } catch (Exception e) {
            //参数错误
            LOGGER.error(String.format("参数错误：%s,异常信息：%s", params, e.getMessage()));
            response.setCode("-1");
            response.setMessage("参数错误");
        }

        try {
            chkParam = chkParam(params);
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",数据处理内部错误，错误信息：", e.getMessage());
            response.setCode("-1");
            response.setMessage("参数错误");
        }
        if (chkParam != null && chkParam.getCode() == -1) {
            //效验失败
            LOGGER.error(String.format("参数错误：%s", chkParam.getMessage()));
            response.setCode("-1");
            response.setMessage(chkParam.getMessage());
        } else {
            //效验成功，开始执行更新变量
            LOGGER.info(String.format("参数必要字段验证通过：%s , gid:%s", params, gid));
            //1.反欺诈变量更新url
            String url = PropertiesUtil.getServiceMap().get("antifraudvarible_updateurl");
            //2.效验url
            if (checkUrl(url)) {
                //url合法
                try {
                    //3.发起请求
                    LOGGER.info(String.format("开始发送请求，请求地址：%s，请求参数：%s", url, params));
                    String reContent = HttpClientUtil.httpPost(url, params, 30 * 1000);
                    LOGGER.info(String.format("接口返回信息：%s", reContent));
                    Map<String, Object> m = JsonUtil.json2Object(reContent, Map.class);

                    if (m.get("status").toString().equalsIgnoreCase("success")) {
                        response.setCode("0");
                        response.setMessage("SUCCESS");
                    } else {
                        response.setCode("-4");
                        response.setMessage("接口返回异常，异常信息" + m.get("msg"));
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("请求异常,异常信息：%s", e.getClass().getName()));
                    response.setCode("-2");
                    response.setMessage("外部接口连接异常");
                }
            } else {
                //url不合法
                LOGGER.error(String.format("url不合法：%s", url));
                response.setCode("-1");
                response.setMessage("url不合法");

            }

        }

        return response;
    }

    @Override
    public BaseResponse count(HttpServletRequest request) {

        LOGGER.info(String.format("进入反欺诈计数"));
        String gid = null;
        BaseResponse response = new BaseResponse();
        Map<String, String> params = null;
        ResponseCodeEnum chkParam = null;
        try {
            //参数校验gid,status;
            params = getParamFromRequest(request);
            gid = String.valueOf(params.get(ProcessContextEnum.GID.val));
            LOGGER.info(String.format("参数格式验证通过：%s , gid:%s", params, gid));
        } catch (Exception e) {
            //参数错误
            LOGGER.error(String.format("参数错误：%s,异常信息：%s", params, e.getMessage()));
            response.setCode("-1");
            response.setMessage("参数错误");
        }
        try {
            chkParam = chkParamCount(params);
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",数据处理内部错误，错误信息：", e.getMessage());
            response.setCode("-1");
            response.setMessage("参数错误");
        }

        if (chkParam != null && chkParam.getCode() == -1) {
            //效验失败
            LOGGER.error(String.format("参数错误：%s", chkParam.getMessage()));
            response.setCode("-1");
            response.setMessage(chkParam.getMessage());
        } else {
            //效验成功，开始执行计数
            Date data = new Date();
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.put("createTime", time.format(data));
            System.out.println(params);
            LOGGER.info(String.format("参数必要字段验证通过：%s , gid:%s", params, gid));
            //1.反欺诈计数url
            String url = PropertiesUtil.getServiceMap().get("antifraudvarible_counturl");
            //2.效验url
            if (checkUrl(url)) {
                //url合法
                try {
                    LOGGER.info(String.format("开始发送请求，请求地址：%s，请求参数：%s", url, params));
                    String reContent = HttpClientUtil.httpPost(url, params, 30 * 1000);
                    LOGGER.info(String.format("接口返回信息：%s", reContent));
                    Map<String, Object> m = JsonUtil.json2Object(reContent, Map.class);
                    if (m.get("status").toString().equalsIgnoreCase("success")) {
                        response.setCode("0");
                        response.setMessage("SUCCESS");
                    } else {
                        response.setCode("-4");
                        response.setMessage("接口返回异常，异常信息" + m.get("msg"));
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("请求异常,异常信息：%s", e.getClass().getName()));
                    response.setCode("-2");
                    response.setMessage("外部接口连接异常");
                }

            } else {
                //url不合法
                LOGGER.error(String.format("url不合法：%s", url));
                response.setCode("-1");
                response.setMessage("url不合法");
            }
        }


        return response;
    }

    @Override
    public BaseResponse select(HttpServletRequest request) {
        LOGGER.info(String.format("进入反欺诈计数查询"));
        BaseResponse baseResponse = new BaseResponse();
        /*ResponseData responseData = new ResponseData();
        //url
        String url = PropertiesUtil.getServiceMap().get("std_data_urlss");

        String seqNo = null;

        Map<String, String> params = null;
        ResponseCodeEnum chkParam = null;
        String reContent = null;
        HashMap map = new HashMap();
        HashMap inParameter = new HashMap();

        try {
            params = getParamFromRequest(request);
            seqNo = String.valueOf(params.get(ProcessContextEnum.SEQ_NO.val));
            LOGGER.info(String.format("参数格式验证通过：%s , seqNo:%s", params, seqNo));
        } catch (Exception e) {
            //参数错误
            LOGGER.error(String.format("参数错误：%s,异常信息：%s", params, e.getMessage()));
            baseResponse.setCode("-1");
            baseResponse.setMessage("参数错误");
            baseResponse.setData(new ArrayList<>());
        }
        try {
            chkParam = chkParamSelect(params);
        } catch (Exception e) {
            LOGGER.error("seqNo:" + seqNo + ",数据处理内部错误，错误信息：", e.getMessage());
            baseResponse.setCode("-1");
            baseResponse.setMessage("参数错误");
            baseResponse.setData(new ArrayList<>());
        }
        if (chkParam != null && chkParam.getCode() == -1) {
            //效验失败
            LOGGER.error(String.format("参数错误：%s", chkParam.getMessage()));
            baseResponse.setCode("-1");
            baseResponse.setMessage(chkParam.getMessage());
            baseResponse.setData(new ArrayList<>());

        } else {
            LOGGER.info(String.format("参数必要字段验证通过：%s , seqNo:%s", params, seqNo));

            map.put("mid", "antifraudall");
            map.put("cmid", "antifraudall");
            map.put("seqNo", params.get("seqNo"));
            map.put("bid", params.get("bid"));
            map.put("type", params.get("type"));
            map.put("data", params.get("data"));
            inParameter.put("params", JsonUtil.toJson(map));
            LOGGER.info(String.format("开始发送请求，请求地址：%s，请求参数：%s", url, inParameter));
            try {
                reContent = HttpClientUtil.httpPost(url, inParameter, 300 * 1000);
            } catch (Exception e) {
                LOGGER.error(String.format("请求异常,异常信息：%s", e.getClass().getName()));
                baseResponse.setCode("-2");
                baseResponse.setMessage("外部接口连接异常");
                baseResponse.setData(new ArrayList<>());
            }

        }
        try {

            responseData = JsonUtil.json2Object(reContent, ResponseData.class);
            if (responseData != null) {

                if (responseData.getData().size() != 0) {
                    LOGGER.info("gid:{},获取的反欺诈数据大小为:{}", seqNo, responseData.getData().size());
                    *//* 入库*//*
                    List<TableData> data = responseData.getData();
                    ResponseData finalResponse = responseData;
                    String finalGid = seqNo;
                    try {
                        *//* 入库*//*
                        new Thread() {
                            @Override
                            public void run() {
                                for (TableData tableData : data)
                                    data2DBServiceImpl.data2DBHandle(finalGid, finalResponse.getRid(), finalResponse.getStep(), tableData.getDbName(), tableData.getTableName(), tableData.getData(), new HashMap<>());
                            }
                        }.start();
                        *//* 入库*//*
                    } catch (Exception e) {
                        LOGGER.error(String.format("入库失败,异常信息：%s", e.getClass().getName()));
                    }

                    //返回数据
                    baseResponse.setCode("0");
                    baseResponse.setMessage("SUCCESS");
                    List list = new ArrayList();
                    for (TableData tableData : data) {
                        for (Map<String, Object> m : tableData.getData()) {
                            //公共的bid="all"
                            if (params.get("type").toString().equals("public")) {
                                if (m.get("bid_code").toString().equals("c0000")) {
                                    if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                                        *//*if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                                            String[] dataS = params.get("revar").split(",");
                                            for (String s : dataS) {
                                                if (m.get("name_revar").toString().equals(s)) {
                                                    String[] columns = params.get("columns").split(",");
                                                    HashMap<String, Object> ma = new HashMap<>();
                                                    for (String column : columns) {
                                                        if (m.get(column) != null) {
                                                            ma.put(column, m.get(column));
                                                        }

                                                    }
                                                    list.add(ma);
                                                }
                                            }
                                        } else {*//*
                                        String[] dataS = params.get("revar").split(",");
                                        for (String s : dataS) {
                                            if (m.get("name_revar").toString().equals(s)) {
                                                list.add(m);
                                            }
                                        }
                                        *//*}*//*
                                    } else {
                                        if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                                            String[] dataS = params.get("invar").split(",");
                                            for (String s : dataS) {
                                                if (m.get("name_invar").toString().equals(s)) {
                                                    list.add(m);
                                                }
                                            }
                                        }else{
                                            list.add(m);
                                        }


                                        *//*if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                                            String[] columns = params.get("columns").split(",");
                                            HashMap<String, Object> ma = new HashMap<>();
                                            for (String column : columns) {
                                                if (m.get(column) != null) {
                                                    ma.put(column, m.get(column));
                                                }
                                            }
                                            list.add(ma);
                                        } else {
                                            list.add(m);
                                        }*//*

                                    }
                                }
                            } else if (params.get("type").toString().equals("private")) {
                                //私有的bid=""传参bid
                                if (!m.get("bid_code").toString().equals("c0000")) {

                                    if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                                        *//*if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                                            String[] dataS = params.get("revar").split(",");
                                            for (String s : dataS) {
                                                if (m.get("name_revar").toString().equals(s)) {
                                                    String[] columns = params.get("columns").split(",");
                                                    HashMap<String, Object> ma = new HashMap<>();
                                                    for (String column : columns) {
                                                        if (m.get(column) != null) {
                                                            ma.put(column, m.get(column));
                                                        }
                                                    }
                                                    list.add(ma);
                                                }
                                            }
                                        } else {*//*
                                        String[] dataS = params.get("revar").split(",");
                                        for (String s : dataS) {
                                            if (m.get("name_revar").toString().equals(s)) {
                                                list.add(m);
                                            }
                                        }
                                        *//* }*//*

                                    } else {
                                        if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                                            String[] dataS = params.get("invar").split(",");
                                            for (String s : dataS) {
                                                if (m.get("name_invar").toString().equals(s)) {
                                                    list.add(m);
                                                }
                                            }
                                        }else{
                                            list.add(m);
                                        }
                                        *//*if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                                            String[] columns = params.get("columns").split(",");
                                            HashMap<String, Object> ma = new HashMap<>();
                                            for (String column : columns) {
                                                if (m.get(column) != null) {
                                                    ma.put(column, m.get(column));
                                                }
                                            }
                                            list.add(ma);
                                        } else {
                                            list.add(m);
                                        }*//*
                                    }
                                }

                            } else if (params.get("type").toString().equals("all")) {
                                if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                                    *//*if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                                        String[] dataS = params.get("revar").split(",");
                                        for (String s : dataS) {
                                            if (m.get("name_revar").toString().equals(s)) {
                                                String[] columns = params.get("columns").split(",");
                                                HashMap<String, Object> ma = new HashMap<>();
                                                for (String column : columns) {
                                                    if (m.get(column) != null) {
                                                        ma.put(column, m.get(column));
                                                    }
                                                }
                                                list.add(ma);
                                            }
                                        }
                                    } else {*//*
                                    String[] dataS = params.get("revar").split(",");
                                    for (String s : dataS) {
                                        if (m.get("name_revar").toString().equals(s)) {
                                            list.add(m);
                                        }
                                    }
                                    *//*}*//*

                                } else {
                                    if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                                        String[] dataS = params.get("invar").split(",");
                                        for (String s : dataS) {
                                            if (m.get("name_invar").toString().equals(s)) {
                                                list.add(m);
                                            }
                                        }
                                    }else{
                                        list.add(m);
                                    }

                                    *//*if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                                        String[] columns = params.get("columns").split(",");
                                        HashMap<String, Object> ma = new HashMap<>();
                                        for (String column : columns) {
                                            if (m.get(column) != null) {
                                                ma.put(column, m.get(column));
                                            }
                                        }
                                        list.add(ma);
                                    } else {
                                        list.add(m);
                                    }*//*
                                }
                            }
                        }

                        baseResponse.setData(list);
                    }
                    if (list.size() == 0) {
                        Map<String, String> noneMap = new HashMap<>();
                        noneMap.put("bid_code", "");
                        noneMap.put("seqNo", "");
                        noneMap.put("name_revar", "");
                        noneMap.put("name_invar", "");
                        noneMap.put("value_revar", "");
                        noneMap.put("id", "");
                        noneMap.put("bid", "");
                        noneMap.put("online_report_id", "");
                        noneMap.put("uuid", "");
                        noneMap.put("query_time", "");
                        list.add(noneMap);
                    }
                } else {
                    baseResponse.setCode("-1");
                    baseResponse.setMessage("失败，接口无返回数据");
                    baseResponse.setData(new ArrayList<>());
                    LOGGER.info(String.format("查询失败，接口无返回数据：%s , seqNo:%s", params, seqNo));
                }
            } else {
                baseResponse.setCode("-1");
                baseResponse.setMessage("失败，接口返回失败");
                baseResponse.setData(new ArrayList<>());
                LOGGER.info(String.format("查询失败：%s , seqNo:%s", params, seqNo));
            }
        } catch (Exception e) {
            baseResponse.setCode("-1");
            baseResponse.setMessage("失败，接口无返回数据");
            baseResponse.setData(new ArrayList<>());
            LOGGER.info("seqNo{},请求的反欺诈结果异常，异常信息{}", seqNo, e);
        }
*/

        return baseResponse;
    }

    /*@Override*/
    /*public BaseResponse selects(HttpServletRequest request) {
        LOGGER.info(String.format("进入反欺诈计数更新"));
        System.out.println(data2DBServiceImpl);
        //url
        String url = PropertiesUtil.getServiceMap().get("std_data_url");
        String gid = null;
        ResponseData response = new ResponseData();
        BaseResponse r = new BaseResponse();
        Map<String, String> params = null;
        ResponseCodeEnum chkParam = null;
        String reContent = null;
        HashMap map = new HashMap();
        HashMap inParameter = new HashMap();
        try {
            //参数校验gid,status;
            params = getParamFromRequest(request);
            gid = String.valueOf(params.get(ProcessContextEnum.GID.val));
            LOGGER.info(String.format("参数格式验证通过：%s , gid:%s", params, gid));
        } catch (Exception e) {
            //参数错误
            LOGGER.error(String.format("参数错误：%s,异常信息：%s", params, e.getMessage()));
            response.setCode("-1");
            response.setMsg("参数错误");
        }
        try {
            chkParam = chkParamSelect(params);
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",数据处理内部错误，错误信息：", e.getMessage());
            response.setCode("-1");
            response.setMsg("参数错误");
        }
        if (chkParam != null && chkParam.getCode() == -1) {
            //效验失败
            LOGGER.error(String.format("参数错误：%s", chkParam.getMessage()));
            response.setCode("-1");
            response.setMsg(chkParam.getMessage());
        } else {
            try {
                //bid为空 查询所有
                if (params.get(HttpConstant.BID) == null || StringUtils.isBlank(params.get(HttpConstant.BID).toString())) {
                    LOGGER.info(String.format("参数bid为空，查询所有变量gid:%s", gid));
                    //效验成功，开始执行查询全部
                    LOGGER.info(String.format("参数必要字段验证通过：%s , gid:%s", params, gid));
                    LOGGER.info(String.format("开始发送请求，请求地址：%s，请求参数：%s", url, params));
                    map.put("mid", "antifraudall");
                    map.put("cmid", "antifraudall");
                    map.put("gid", params.get("gid"));
                    inParameter.put("params", JsonUtil.toJson(map));
                    reContent = HttpClientUtil.httpPost(url, inParameter, 300 * 1000);


                } else {
                    LOGGER.info(String.format("参数bid不空，查询部分变量gid:%s", gid));
                    //效验成功，开始执行查询某些
                    LOGGER.info(String.format("参数必要字段验证通过：%s , gid:%s", params, gid));

                    map.put("mid", "antifraudportion");
                    map.put("cmid", "antifraudportion");
                    map.put("gid", params.get("gid"));
                    map.put("bid", params.get("bid"));
                    map.put("data", params.get("data"));
                    inParameter.put("params", JsonUtil.toJson(map));
                    reContent = HttpClientUtil.httpPost(url, inParameter, 30 * 1000);
                }
            } catch (Exception e) {
                LOGGER.error(String.format("请求异常,异常信息：%s", e.getClass().getName()));
                response.setCode("-2");
                response.setMsg("外部接口连接异常");
            }

        }

        try {
            response = JsonUtil.json2Object(reContent, ResponseData.class);

            if (response != null) {
                if (response.getData() != null) {
                    List<TableData> data = response.getData();
                    ResponseData finalResponse = response;
                    String finalGid = gid;
                    r.setCode("0");
                    r.setMessage("SUCCESS");
                    List list = new ArrayList();
                    for (TableData tableData : data) {
                        for (Map<String, Object> m : tableData.getData()) {
                            list.add(m);

                        }
                        r.setData(list);
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            for (TableData tableData : data)
                                data2DBServiceImpl.data2DBHandle(finalGid, finalResponse.getStep(), tableData.getDbName(), tableData.getTableName(), tableData.getData(), new HashMap<>());
                        }
                    }.start();

                }
            }
        } catch (Exception e) {
            response.setCode("-3");
            response.setMsg("请求返回异常");
            LOGGER.error(String.format("请求返回异常,异常信息：%s", e.getClass().getName()));
        }

        return r;
    }*/

    //从Request对象里获取参数param
    private Map<String, String> getParamFromRequest(HttpServletRequest request) {

        Map<String, String> map;
        try {
            String paramJson = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
            if (StringUtils.isBlank(paramJson)) {
                throw new RuntimeException("参数为空");
            }
            map = JsonUtil.json2Object(paramJson, Map.class);
            return map;
        } catch (Exception e) {
            LOGGER.error(String.format("参数解析错误，错误信息：%s", e.getMessage()));
            throw new RuntimeException(e.getMessage());
        }
    }

    //效验参数中是否有gid和status
    private ResponseCodeEnum chkParam(Map<String, String> requestMap) {
        if (requestMap == null) {
            return ResponseCodeEnum.paramFail("参数param为空！");
        }
        if (requestMap.get(HttpConstant.MSG_STATUS) == null || StringUtils.isBlank(requestMap.get(HttpConstant.MSG_STATUS).toString())) {
            return ResponseCodeEnum.paramFail("没有status！");
        }
        if (requestMap.get(HttpConstant.GLOBE_ID) == null || StringUtils.isBlank(requestMap.get(HttpConstant.GLOBE_ID).toString())) {
            return ResponseCodeEnum.paramFail("没有gid！");
        }
        return null;
    }

    private ResponseCodeEnum chkParamCount(Map<String, String> requestMap) {
        if (requestMap == null) {
            return ResponseCodeEnum.paramFail("参数param为空！");
        }
        if (requestMap.get(HttpConstant.GLOBE_ID) == null || StringUtils.isBlank(requestMap.get(HttpConstant.GLOBE_ID).toString())) {
            return ResponseCodeEnum.paramFail("没有gid！");
        }
        if (requestMap.get(HttpConstant.UUID) == null || StringUtils.isBlank(requestMap.get(HttpConstant.UUID).toString())) {
            return ResponseCodeEnum.paramFail("没有uuid！");
        }
        if (requestMap.get(HttpConstant.BID) == null || StringUtils.isBlank(requestMap.get(HttpConstant.BID).toString())) {
            return ResponseCodeEnum.paramFail("没有bid！");
        }
        return null;
    }


    private ResponseCodeEnum chkParamSelect(Map<String, String> requestMap) {
        if (requestMap == null) {
            return ResponseCodeEnum.paramFail("参数param为空！");
        }
        if (requestMap.get(HttpConstant.SEQ_NO) == null || StringUtils.isBlank(requestMap.get(HttpConstant.SEQ_NO).toString())) {
            return ResponseCodeEnum.paramFail("没有seqNo！");
        }
        if (requestMap.get(HttpConstant.BID) == null || StringUtils.isBlank(requestMap.get(HttpConstant.BID).toString())) {
            return ResponseCodeEnum.paramFail("没有bid！");
        }
        if (requestMap.get(HttpConstant.TYPE) == null || StringUtils.isBlank(requestMap.get(HttpConstant.TYPE).toString())) {
            return ResponseCodeEnum.paramFail("没有type！");
        }
        if (requestMap.get("type") != null && !StringUtils.isBlank(requestMap.get("type").toString())) {
            if (!requestMap.get("type").equals("all") && !requestMap.get("type").equals("public") && !requestMap.get("type").equals("private")) {
                return ResponseCodeEnum.paramFail("type值只能为all、public、private！");
            }
            return null;
        }
        return null;
    }

    //效验url是否合法，合法返回true
    private boolean checkUrl(String url) {
        //正则
        Pattern pattern = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher isUrl = pattern.matcher(url);
        if (!isUrl.matches()) {
            return false;
        } else {
            return true;
        }
    }

    /*private String varFilter() {

        //公共的bid="all"
        if (params.get("type").toString().equals("public")) {
            if (m.get("bid_code").toString().equals("c0000")) {
                if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                    if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                        String[] dataS = params.get("revar").split(",");
                        for (String s : dataS) {
                            if (m.get("name_revar").toString().equals(s)) {
                                String[] columns = params.get("columns").split(",");
                                HashMap<String, Object> ma = new HashMap<>();
                                for (String column : columns) {
                                    if (m.get(column) != null) {
                                        ma.put(column, m.get(column));
                                    }

                                }
                                list.add(ma);
                            }
                        }
                    } else {
                        String[] dataS = params.get("revar").split(",");
                        for (String s : dataS) {
                            if (m.get("name_revar").toString().equals(s)) {
                                list.add(m);
                            }
                        }
                    }
                } else {
                    if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                        String[] columns = params.get("columns").split(",");
                        HashMap<String, Object> ma = new HashMap<>();
                        for (String column : columns) {
                            if (m.get(column) != null) {
                                ma.put(column, m.get(column));
                            }
                        }
                        list.add(ma);
                    } else {
                        list.add(m);
                    }

                }
            }
        } else if (params.get("type").toString().equals("private")) {
            //私有的bid=""传参bid
            if (!m.get("bid_code").toString().equals("c0000")) {

                if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                    if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                        String[] dataS = params.get("revar").split(",");
                        for (String s : dataS) {
                            if (m.get("name_revar").toString().equals(s)) {
                                String[] columns = params.get("columns").split(",");
                                HashMap<String, Object> ma = new HashMap<>();
                                for (String column : columns) {
                                    if (m.get(column) != null) {
                                        ma.put(column, m.get(column));
                                    }
                                }
                                list.add(ma);
                            }
                        }
                    } else {
                        String[] dataS = params.get("revar").split(",");
                        for (String s : dataS) {
                            if (m.get("name_revar").toString().equals(s)) {
                                list.add(m);
                            }
                        }
                    }

                } else {
                    if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                        String[] columns = params.get("columns").split(",");
                        HashMap<String, Object> ma = new HashMap<>();
                        for (String column : columns) {
                            if (m.get(column) != null) {
                                ma.put(column, m.get(column));
                            }
                        }
                        list.add(ma);
                    } else {
                        list.add(m);
                    }
                }
            }

        } else if (params.get("type").toString().equals("all")) {
            if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                    String[] dataS = params.get("revar").split(",");
                    for (String s : dataS) {
                        if (m.get("name_revar").toString().equals(s)) {
                            String[] columns = params.get("columns").split(",");
                            HashMap<String, Object> ma = new HashMap<>();
                            for (String column : columns) {
                                if (m.get(column) != null) {
                                    ma.put(column, m.get(column));
                                }
                            }
                            list.add(ma);
                        }
                    }
                } else {
                    String[] dataS = params.get("revar").split(",");
                    for (String s : dataS) {
                        if (m.get("name_revar").toString().equals(s)) {
                            list.add(m);
                        }
                    }
                }

            } else {
                if (params.get("columns") != null && params.get("columns").toString().length() != 0) {
                    String[] columns = params.get("columns").split(",");
                    HashMap<String, Object> ma = new HashMap<>();
                    for (String column : columns) {
                        if (m.get(column) != null) {
                            ma.put(column, m.get(column));
                        }
                    }
                    list.add(ma);
                } else {
                    list.add(m);
                }
            }
        }


        return null;
    }*/
}
