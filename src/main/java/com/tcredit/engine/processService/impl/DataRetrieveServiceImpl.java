package com.tcredit.engine.processService.impl;

import com.google.common.collect.Maps;
import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.constants.ResponseConstants;
import com.tcredit.engine.data_process.DataProcessor;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.processService.DataRetrieveService;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.ResponseCodeEnum;
import com.tcredit.engine.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-20 14:25
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-20 14:25
 * @updatedRemark:
 * @version:
 */
@Service("dataRetrieveServiceImpl")
public class DataRetrieveServiceImpl implements DataRetrieveService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataRetrieveServiceImpl.class);

    /**
     * 将参数解析为RetrieveEntity
     *
     * @param request
     * @return
     */
    @Override
    public RetrieveEntity paramParse(HttpServletRequest request) {
        if (request == null) return null;
        String paramJson = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
        RetrieveEntity entity = JsonUtil.json2Object(paramJson, RetrieveEntity.class);

        if (entity != null && StringUtils.isNotBlank(entity.tblName)) {
            return entity;
        } else {
            return null;
        }
    }
    public RetrieveEntity paramGet(HttpServletRequest request) {
        if (request == null) return null;
        String paramJson = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
        RetrieveEntity entity = JsonUtil.json2Object(paramJson, RetrieveEntity.class);

        if (entity != null ) {
            return entity;
        } else {
            return null;
        }
    }

    /**
     * 查询数据服务
     *
     * @param entity
     * @return
     */
    @Override
    public BaseResponse<List<Map<String, Object>>> dataRetrieve(RetrieveEntity entity) {
        ResponseCodeEnum chkParam = chkParam(entity);
        if (chkParam != null) {
            return new BaseResponse<>(chkParam);
        }

        /**
         * 参数校验成功
         */
        return retrieve(entity);
    }

    /**
     * 查询数据
     *
     * @param entity
     * @return
     */
    private BaseResponse<List<Map<String, Object>>> retrieve(RetrieveEntity entity) {
        List<Map<String, Object>> data = null;
        try {
            data = DataProcessor.retrieve(entity);
            if (data == null || data.isEmpty()) {
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
        if (entity == null || StringUtils.isBlank(entity.tblName)) {
            return ResponseCodeEnum.paramFail("参数错误,无法解析tblName");
        } else if (StringUtils.isBlank(entity.gid) && StringUtils.isBlank(entity.rid)) {
            return ResponseCodeEnum.paramFail("参数错误,查询条件rid|gid为空");
        }

        return null;
    }


    @Override
    public BaseResponse<Map<String, List<Map<String, Object>>>> manyTableDataRetrieve(RetrieveEntity entity) {
        ResponseCodeEnum chkParam = chkParam(entity);
        if (chkParam != null) {
            return new BaseResponse<>(chkParam);
        }

        /**
         * split 一定不为null,且split至少有一个元素
         */
        try {
            String[] split = entity.tblName.split(",");
            Map<String, List<Map<String, Object>>> tblDatas = Maps.newHashMap();
            for (String tblName : split) {
                entity.tblName = tblName.trim();
                if (StringUtils.isNotBlank(entity.tblName)) {
                    List<Map<String, Object>> retrieve = DataProcessor.retrieve(entity);
                    if (retrieve != null && !retrieve.isEmpty()) {
                        tblDatas.put(tblName, retrieve);
                    }
                }
            }

            if (tblDatas == null || tblDatas.isEmpty()) {
                return new BaseResponse<>(ResponseCodeEnum.noData("无数据"));
            }
            return new BaseResponse(ResponseConstants.SUCCESS, ResponseConstants.SUCCESS_MESSAGE, tblDatas);
        } catch (Exception e) {
            LOGGER.error(String.format("查询，步骤:%s,数据库:%s,表:%s,出错，错误信息:%s", entity.step, entity.db, entity.tblName, e));
            return new BaseResponse<>(ResponseCodeEnum.innerFail(e.getMessage()));
        }

    }

    @Override
    public BaseResponse<List<Map<String, Object>>> retrieveAntifraud(HttpServletRequest request) {
        RetrieveEntity entity = paramGet(request);
        entity.tblName = "std_std_ip_ifr01_antifraud_kvarval";
        BaseResponse<List<Map<String, Object>>> baseResponse = dataRetrieve(entity);
        BaseResponse<List<Map<String, Object>>> baseR = new BaseResponse<>();
        if (baseResponse.getCode().equals("0")) {
            Map<String, String> params = getParamFromRequest(request);
            List<Map<String, Object>> list = baseResponse.getData();
            List<Map<String, Object>> reList = new ArrayList<>();
            for (Map<String, Object> m : list) {
                Map<String, Object> reM= filterResult(m, params);
                if(reM!=null){
                    reList.add(reM);
                }

            }
            baseR.setData(reList);
            if (reList.size() == 0) {
                Map<String, Object> noneMap = new HashMap<>();
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
                reList.add(noneMap);
                baseR.setData(reList);
            }
        }else if(baseResponse.getCode().equals("-5")){
            List<Map<String, Object>> reList = new ArrayList<>();
            Map<String, Object> noneMap = new HashMap<>();
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
            reList.add(noneMap);
            baseR.setData(reList);
        }

        return baseR;
    }

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

    public Map<String, Object> filterResult(Map<String, Object> m, Map<String, String> params) {
        if (params.get("type").equals("public")) {
            if (m.get("bid_code").equals("all")||m.get("bid_code").equals("c0000")) {
                if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                    if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                        String[] dataS = params.get("invar").split(",");
                        String[] dataR = params.get("revar").split(",");
                        for (String r : dataR) {
                            for (String s : dataS) {
                                if (m.get("name_invar").toString().equals(s) && m.get("name_revar").toString().equals(r)) {
                                    return m;
                                }
                            }
                        }
                    } else {
                        String[] dataS = params.get("revar").split(",");
                        for (String s : dataS) {
                            if (m.get("name_revar").toString().equals(s)) {
                                return m;
                            }
                        }
                    }
                } else {
                    if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                        String[] dataS = params.get("invar").split(",");
                        for (String s : dataS) {
                            if (m.get("name_invar").toString().equals(s)) {
                                return m;
                            }
                        }
                    } else {
                        return m;
                    }
                }
            }
        } else if (params.get("type").equals("private")) {
            if (!m.get("bid_code").equals("all")&&!m.get("bid_code").equals("c0000")) {
                if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                    if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                        String[] dataS = params.get("invar").split(",");
                        String[] dataR = params.get("revar").split(",");
                        for (String r : dataR) {
                            for (String s : dataS) {
                                if (m.get("name_invar").toString().equals(s) && m.get("name_revar").toString().equals(r)) {
                                    return m;
                                }
                            }
                        }
                    } else {
                        String[] dataS = params.get("revar").split(",");
                        for (String s : dataS) {
                            if (m.get("name_revar").toString().equals(s)) {
                                return m;

                            }
                        }
                    }
                } else {
                    if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                        String[] dataS = params.get("invar").split(",");
                        for (String s : dataS) {
                            if (m.get("name_invar").toString().equals(s)) {
                                return m;
                            }
                        }
                    } else {
                        return m;
                    }
                }
            }

        } else if (params.get("type").equals("all")) {
            if (params.get("revar") != null && params.get("revar").toString().length() != 0) {
                if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                    String[] dataS = params.get("invar").split(",");
                    String[] dataR = params.get("revar").split(",");
                    for (String r : dataR) {
                        for (String s : dataS) {
                            if (m.get("name_invar").toString().equals(s) && m.get("name_revar").toString().equals(r)) {
                                return m;
                            }
                        }
                    }
                } else {
                    String[] dataS = params.get("revar").split(",");
                    for (String s : dataS) {
                        if (m.get("name_revar").toString().equals(s)) {
                            return m;

                        }
                    }
                }
            } else {
                if (params.get("invar") != null && params.get("invar").toString().length() != 0) {
                    String[] dataS = params.get("invar").split(",");
                    for (String s : dataS) {
                        if (m.get("name_invar").toString().equals(s)) {
                            return m;
                        }
                    }
                } else {
                    return m;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        String json = "{\"tblName\":\"std_baidu_blacklist\",\"columns\":[\"gid\",\"name\",\"sex\"]}";
        String json = "{\"tblName\":\"std_baidu_blacklist\"}";


    }
}
