/*
package com.tcredit.engine.processService.impl;

import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextHolder;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.exception.ParamException;
import com.tcredit.engine.processService.DataProcessingService;
import com.tcredit.engine.processService.DataRetrieveService;
import com.tcredit.engine.processService.DataService;
import com.tcredit.engine.response.*;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PropertiesUtil;
import com.tcredit.engine.util.httpClient.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@Service
public class DataOLDServiceImpl implements DataService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataOLDServiceImpl.class);

    @Autowired
    DataProcessingService dataProcessingService;

    @Resource(name = "dataRetrieveServiceImpl")
    DataRetrieveService dataRetrieveService;

    @Override
    public DataResponse getDataProd(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        ProcessContextHolder.setDataHandlerSynchronized(true);

        LOGGER.info("数据产品开始");

        DataResponse radarResponse = new DataResponse();
        radarResponse.setData(new HashMap<>());


        //入参
        Map<String, Object> params = null;
        String gid = null;
        String mid = null;
        try {
            params = getParamFromRequest(request);
            LOGGER.info("入参是:" + params);
            gid = String.valueOf(params.get(ProcessContextEnum.GID.val));
            LOGGER.info("gid:" + gid + ",完成参数转化并开始校验");

            String chkParam = chkParam(params);
            if (chkParam != null) {
                radarResponse.setGid(gid);
                radarResponse.setMessage(chkParam);
                LOGGER.info("gid:" + gid + ",效验错误，错误信息：" + chkParam + " 入参为：" + params);
                return radarResponse;
            } else {
                LOGGER.info("gid:" + gid + ",效验成功，开始处理" + params);
                mid = String.valueOf(params.get("prod_code"));

                //访问UUIDService生产uuid
                String uuidUrl = PropertiesUtil.getServiceMap().get("uuid_service");
                Map<String, String> ma = new HashMap<>();
                ma.put("name", String.valueOf(params.get("name")));
                ma.put("cardNo", String.valueOf(params.get("idcard")));
                String reContent = HttpClientUtil.httpPost(uuidUrl, ma, 30 * 1000);
                HashMap uMap = JsonUtil.json2Object(reContent, HashMap.class);
                LOGGER.info("gid:" + gid + ",访问UUIDService返回信息，" + reContent + " ，参数：" + ma);

                if (uMap != null) {
                    if (uMap.get("code").equals("200")) {
                        radarResponse.setMessage("接口调用成功");
                        params.put("uuid", String.valueOf(uMap.get("uniqueId")));
                        params.put("mid", mid);

                        //处理结果
                        OutResponse outResponse = dataProcessingService.process(params);
                        //如果code为0则表示处理成功
                        if (outResponse.getCode().equals("0")) {
                            String rid = outResponse.getRid();
                            String tableName = PropertiesUtil.getDataProdMap().get(mid + ".tableName");
                            LOGGER.info("gid:" + gid + ",处理成功，开始加工返回数据");
                            RetrieveEntity retrieveEntity = new RetrieveEntity();
                            retrieveEntity.rid = rid;//要查询的rid
                            retrieveEntity.tblName = tableName;
                            //要查询的表名称

                            //遍历结果集
                            List<Map<String, Object>> list = dataRetrieveService.dataRetrieve(retrieveEntity).getData();
                            if (list != null && list.size() != 0) {
                                LOGGER.info("rid:" + rid + ",表：" + tableName + " 查询结果长度为：" + list.size());
                                LOGGER.info("gid:" + gid + ",返回结果集中剔除" + PropertiesUtil.getDataProdMap().get(mid + ".filter"));


                                //成功信息返回

                                radarResponse.setGid(gid);
                                radarResponse.setModuleId(PropertiesUtil.getDataProdMap().get(mid + ".moduleId"));
                                radarResponse.setStatus("0");
                                radarResponse.setData(mapRid(list, mid));
                                long endTime = System.currentTimeMillis();
                                LOGGER.info("gid:" + gid + ",接口调用成功！总耗时：" + (endTime - startTime));
                            } else {
                                LOGGER.info("gid:" + gid + ",表：" + tableName + " 未查询到相关信息");

                                radarResponse.setMessage("接口调用失败，未查询到表数据");
                                radarResponse.setStatus("-2");
                                return radarResponse;
                            }

                        } else {
                            if (outResponse.getMessage().contains("tid失效")) {
                                radarResponse.setMessage("接口调用失败：tid失效");
                                radarResponse.setStatus("-1");
                                return radarResponse;
                            }
                            radarResponse.setMessage("接口调用失败，处理结果失败：" + outResponse.getMessage());
                            radarResponse.setStatus("-2");
                            return radarResponse;
                        }
                    } else {

                        radarResponse.setMessage("接口调用失败，UUID生成失败，失败信息：" + uMap.get("message"));
                        return radarResponse;
                    }
                } else {
                    radarResponse.setMessage("UUID生成失败");
                    radarResponse.setStatus("-2");
                    return radarResponse;
                }

            }
        } catch (ParamException e) {
            //参数没获取到
            radarResponse.setMessage("接口调用失败：" + e.getMessage());
            radarResponse.setStatus("-1");
            return radarResponse;
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",数据处理内部错误，错误信息：", e);

            radarResponse.setStatus("-2");
            radarResponse.setMessage("数据处理内部错误");
            return radarResponse;
        }


        return radarResponse;
    }

    private String chkParam(Map<String, Object> requestMap) {
        if (requestMap == null) {
            return "参数为空！";
        }
        if (requestMap.get("prod_code") == null || StringUtils.isBlank(requestMap.get("prod_code").toString())) {
            return "缺少参数prod_code";
        }
        String mid = requestMap.get("prod_code").toString();

        String requireds = PropertiesUtil.getDataProdMap().get(mid + ".required");

        if (requireds != null) {
            String[] required = requireds.split(",");
            for (String str : required) {
                if (requestMap.get(str) == null || StringUtils.isBlank(requestMap.get(str).toString())) {
                    return "缺少参数" + str;
                }
            }
        } else {
            return "参数错误，没有匹配到相应模块" + mid;
        }


        return null;
    }

    public Map<String, Object> getParamFromRequest(HttpServletRequest request) throws ParamException {

        Map<String, Object> map;
        try {
            String paramJson = request.getParameter(HttpConstant.HTTP_PARAM_NAME);
            if (StringUtils.isBlank(paramJson)) {
                throw new ParamException("参数为空");
            }
            map = JsonUtil.json2Object(paramJson, Map.class);

            return map;
        } catch (Exception e) {
            LOGGER.error("参数解析错误，错误信息：{}", e);
            throw new ParamException("参数解析错误,请检查参数格式");
        }


    }

    public static Map<String, Object> mapRid(List<Map<String, Object>> list, String mid) {
        //获取要剔除的字段
        String[] ridStr = PropertiesUtil.getDataProdMap().get(mid + ".filter").split(",");
        for (String str : ridStr) {
            list.get(0).remove(str);
        }
        return list.get(0);
    }

    */
/*public static void main(String[] args) {
        RetrieveEntity retrieveEntity = new RetrieveEntity();
        retrieveEntity.gid = "zhangtest36";//要查询的gid
        retrieveEntity.tblName = "var_antifraud_ip_applyradar_r_result";//要查询的表名称
        DataRetrieveServiceImpl dataRetrieveService = new DataRetrieveServiceImpl();
        //遍历结果集
        List<Map<String, Object>> list = dataRetrieveService.retrieve(retrieveEntity).getData();
        System.out.println(mapRid(list, "radar"));
    }*//*

}
*/
