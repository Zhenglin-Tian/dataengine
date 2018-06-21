package com.tcredit.engine.conf.handler.httpHandler;

import com.google.common.collect.Maps;
import com.tcredit.engine.conf.*;
import com.tcredit.engine.conf.handler.Handler;
import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.engine.ProcessEngine;
import com.tcredit.engine.exception.CustomedConnectionException;
import com.tcredit.engine.processService.Data2DBService;
import com.tcredit.engine.processService.impl.Data2DBServiceImpl;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.ResponseCodeEnum;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.DeepCopy;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.MessageUtil;
import com.tcredit.engine.util.httpClient.HttpClientUtil;
import com.tcredit.engine.util.variable.VariableUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 15:57
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 15:57
 * @updatedRemark:
 * @version:
 */
public class HttpHandler implements Handler {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(HttpClientUtil.class);
    public static final String REQ_FAIL_ATTEMPT = "attempt";
    public static final String REQ_FAIL_IGNORE = "ignore";
    private static final String JSON_PARAM = "json";
    private static final String ARRAY_PARAM = "array";
    private static final String MAP = "map";


    private String type = "http";
    private ServiceCallingManager serviceCallingManager;

    @Override
    public String handler(ProcessContextV2 cxt, Step step) {
        if (serviceCallingManager == null || cxt == null) {
            return null;
        }
        ServiceCaller serviceCaller = serviceCallingManager.getServiceCaller();
        if (serviceCaller == null) {
            return null;
        }
        //获取请求参数
        Mapper mapper = serviceCaller.getMapper();
        /**
         * 解析所有参数，并且按需要组装json
         */
        Map<String, String> params = getRequestParam(mapper, cxt, serviceCaller);

        /**
         * send request
         */
        return sendRequest(serviceCaller, cxt, step, params);

    }


    /**
     * 发送请求
     */
    private String sendRequest(ServiceCaller serviceCaller, ProcessContextV2 cxt, Step step, Map<String, String> param) {
        String url = parseUrl(serviceCaller, cxt);
        String gid = cxt.get(ProcessContextEnum.GID).toString();
        LOGGER.info("gid:" + gid + ",查询url,查询key:" + serviceCaller.getUrl() + ",查询url值:" + url + ",请求参数:" + JsonUtil.toJson(param));


        int timeThreshold = serviceCaller.getTimeThreshold() * 1000;
        //默认超时时间设置
        if (timeThreshold == 0) timeThreshold = 2 * 60 * 1000;

        //获取发送请求的方式
        String method = serviceCaller.getMethod();

        //如果请求失败，后续策略选择
        String failurePolicy = serviceCaller.getFailurePolicy();
        //如果尝试重新请求，请求多少次，间隔时间是多少,默认请求一次
        int time = 1;
        int intervalInMili = 1;
        AttemptManager callingManager = serviceCallingManager.getAttemptManager();
        if (callingManager != null) {
            if (callingManager.getTimes() >= 1) {
                time = callingManager.getTimes();
            }
            if (callingManager.getIntervalInMilli() >= 0) {
                intervalInMili = callingManager.getIntervalInMilli();
            }

        }

        String originContent = null;
        ResponseData response = null;
        boolean successReq = true;
        String exceptionMsg = null;


        long startRequestTime = System.currentTimeMillis();
        try {

            int i = 0;
            do {
                i++;
                try {
                    if (StringUtils.isNotBlank(method) && HttpConstant.METHOD_POST.equalsIgnoreCase(method)) {
                        originContent = HttpClientUtil.httpPost(url, param, timeThreshold);
                    } else if (StringUtils.isNotBlank(method) && HttpConstant.METHOD_GET.equalsIgnoreCase(method)) {
                        originContent = HttpClientUtil.httpGet(url, param, timeThreshold);
                    }
                } catch (Exception e) {
                    LOGGER.error("gid:" + gid + "，请求url:" + url + "异常，异常信息：", e);
                    if (e instanceof CustomedConnectionException) {
                        successReq = false;
                        exceptionMsg = e.getMessage();
                        try {
                            Thread.sleep(intervalInMili);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
//              LOGGER.info("gid:" + gid + ",请求url:" + url + ",次数：" + i + ",结果：" + originContent);
            } while (!successReq && i < time && REQ_FAIL_ATTEMPT.equalsIgnoreCase(failurePolicy));

        } finally {
            long endRequestTime = System.currentTimeMillis();
            response = JsonUtil.json2Object(originContent, ResponseData.class);
            if (response != null) {
                if (response.getData() != null) {
                    LOGGER.info(String.format("gid:%s,数据处理步骤:%s,请求耗时:%s,请求url:%s,请求参数:%s,请求结果状态码:%s,信息:%s,请求结果集大小:%s", gid, step.getId(), endRequestTime - startRequestTime,
                            url, JsonUtil.toJson(param), response.getCode(), response.getMsg(), response.getData().size()));
                } else {
                    LOGGER.info(String.format("gid:%s,数据处理步骤:%s,请求耗时:%s,请求url:%s,请求参数:%s,请求结果状态码:%s,信息:%s", gid, step.getId(), endRequestTime - startRequestTime,
                            url, JsonUtil.toJson(param), response.getCode(), response.getMsg()));
                }
            } else {
                LOGGER.error("gid:{},返回结果异常，异常信息：结果为空");
                throw new RuntimeException(String.format("gid:%s,步骤:%s，请求结果异常", gid, step.getId()));
            }
        }

        /**
         * 处理返回结果，只有在返回结果状态为成功的时候才开启处理结果
         */
        try {
            if (StringUtils.isNotBlank(originContent) && chkResult(gid, step.getId(), originContent, url)) {
                dealResponse(originContent, step.getId(), cxt, step);
            } else {
                throw new RuntimeException(step.getId() + "数据处理服务响应异常");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (!successReq) {
            throw new CustomedConnectionException("gid:" + gid + ",发送请求异常,异常信息：" + exceptionMsg);
        }

        /**
         * 成功返回当前数据的报告id，否则返回null
         */
        if (response != null) {
            return originContent;
        } else {
            return null;
        }

    }

    private String parseUrl(ServiceCaller serviceCaller, ProcessContextV2 cxt) {
        //获取请求url
        String key = serviceCaller.getUrl();
        String url = null;
        Object o = cxt.get(ProcessContextEnum.CONFIG);
        if (o != null) {
            Map<String, String> map = (Map<String, String>) o;
            url = map.get(key);
        }
        return url;
    }


    /**
     * 检测返回结果是否成功
     *
     * @param responseContent
     * @return
     */
    private boolean chkResult(String gid, String step, String responseContent, String url) {
        boolean flag = false;
        if (StringUtils.isNotBlank(responseContent)) {
            ResponseData outResponse = JsonUtil.json2Object(responseContent, ResponseData.class);
            if (outResponse != null || StringUtils.isNotBlank(outResponse.getCode())) {
                String code = outResponse.getCode();
                try {
                    int codeI = Integer.parseInt(code);
                    if (codeI == 0) {
                        flag = true;
                    } else {
                        throw new RuntimeException("返回结果数据异常,信息:" + outResponse.getMsg());
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("gid:%s,数据处理步骤:%s,检测返回结果是否成功出错，错误信息：%s", gid, step, e));
                    throw new RuntimeException("检查外部服务请求" + url + "结果异常，异常信息:" + e.getMessage());
                }
            }
        }
        return flag;
    }


    /**
     * 处理返回结果
     *
     * @param responseContent
     * @param cxt
     */
    private void dealResponse(String responseContent, String stepId, ProcessContextV2 cxt, Step step) {
        long startHandleResultTime = System.currentTimeMillis();
        ResponseData outResponse = JsonUtil.json2Object(responseContent, ResponseData.class);

        /**
         * 在执行下一步之前，将上一步返回的数据报告id放进cxt
         */
        String rid = outResponse.getRid();
        if (StringUtils.isNotBlank(rid)) {
            cxt.put(step.getId(), rid);
        }

        ResponseData copy = DeepCopy.copy(outResponse);
        copy.setData(null);
        LOGGER.info("转换之后的返回结果为:" + JsonUtil.toJson(copy));


        String gid = String.valueOf(cxt.get(ProcessContextEnum.GID));
        if (!step.isPersistence()) {
//            ProcessEngine.fetchNextStepAndExcute(stepId, cxt);
            MessageUtil.convertBaseToOut(new BaseResponse<>(ResponseCodeEnum.success("处理成功")));

        } else if (outResponse.getSync() != null && outResponse.getSync()) {

            /**
             * 数据入库操作
             */
            List<TableData> data = outResponse.getData();

            LOGGER.info("获取到的data大小为:" + data.size());
            boolean dbSuccess = true;
            long dbStartTime = System.currentTimeMillis();
            if (data != null) {
                Data2DBService data2DBService = new Data2DBServiceImpl();
                Map<String, Object> map = JsonUtil.pojo2Map(outResponse);
                map.remove("data");
                for (TableData tableData : data) {
                    try {
                        ResponseData response = data2DBService.data2DB(gid, outResponse.getRid(), outResponse.getMid(), outResponse.getStep(), tableData.getDbName(), tableData.getTableName(), tableData.getData(), map, step);
                        if (response == null || Integer.parseInt(response.getCode()) != 0) {
                            throw new RuntimeException(gid + ",步骤id:" + stepId + ",数据入库异常，异常信息：" + response.getMsg());
                        }
                    } catch (Exception e) {
                        LOGGER.error("gid:{},步骤id:{},入库异常，异常信息:{}", gid, stepId, e);
                        dbSuccess = false;
                        cxt.setExceptionFlag(true);
                        break;
                    }
                }


                long dbEndTime = System.currentTimeMillis();
                if (dbSuccess) {
                    LOGGER.info(String.format("gid:%s,步骤id:%s,入库成功,耗时:%s", gid, stepId, (dbEndTime - dbStartTime)));

                    /**
                     * 获取后续步骤并处理
                     */
//                    ProcessEngine.fetchNextStepAndExcute(stepId, cxt);
                }

            }

        }
        long endHandleResultTime = System.currentTimeMillis();
        LOGGER.info(String.format("gid:%s,步骤id:%s,处理结果集耗时:%s", gid, stepId, endHandleResultTime - startHandleResultTime));

    }


    /**
     * 参数解析
     *
     * @param mapper
     * @return
     */
    private Map<String, String> getRequestParam(Mapper mapper, ProcessContextV2 cxt, ServiceCaller serviceCaller) {
        Map<String, String> map = Maps.newHashMap();
        String paramFormType = serviceCaller.getParamForm().trim();
        List<Mapping> mappings = mapper.getMappings();
        if (mapper != null && StringUtils.isNotBlank(paramFormType)) {
            //如果参数类型是map，则是key-value格式的普通map
            if (paramFormType.toLowerCase().startsWith(MAP)) {
                for (Mapping m : mappings) {
                    if (m != null) {
                        String source = m.getSource();
                        String val = VariableUtil.parse(source, cxt);
                        /**
                         * 如果该参数值为必须，这个地方应该校验，校验不通过则抛出异常
                         */
                        if (val != null && StringUtils.isNotBlank(val) && !"null".equals(val.toLowerCase())) {
                            map.put(m.getTarget(), val);
                        }
                    }
                }
            } else if (paramFormType.toLowerCase().startsWith(JSON_PARAM)) {
                //如果参数类型是json，则是key-value格式的map，value有可能是json
                JSONObject jsonObject = new JSONObject();
                for (Mapping m : mappings) {
                    if (m != null) {
                        String type = m.getType();
                        //如果type不为空说明，是json格式的参数，需要拼装
                        if (StringUtils.isNotBlank(type)) {
                            getTypeParam(type, cxt, serviceCaller);
                        }

                        String source = m.getSource();
                        String val = VariableUtil.parse(source, cxt);
                        /**
                         * 如果该参数值为必须，这个地方应该校验，校验不通过则抛出异常
                         */
                        if (val != null && StringUtils.isNotBlank(val) && !"null".equals(val.toLowerCase())) {
                            //如果type不为空说明，是json格式的参数，需要拼装
                            if (type != null) {
                                //jsonObject
                                if (type.equals(JSON_PARAM)) {
                                    JSONObject json = JSONObject.fromObject(val);
                                    jsonObject.put(m.getTarget(), json);
                                } else if (type.equals(ARRAY_PARAM)) {
                                    //JsonArray
                                    JSONArray jsonArray = JSONArray.fromObject(val);
                                    jsonObject.put(m.getTarget(), jsonArray);
                                }
                            } else {
                                //type为空key-value
                                jsonObject.put(m.getTarget(), val);
                            }
                        }
                    }
                }
                int i = paramFormType.indexOf("(");
                if (i > 0) {
                    //发送请求时候的参数名称
                    String paramName = paramFormType.substring(i + 1, paramFormType.length() - 1);

                    map.put(paramName, jsonObject.toString());
                }

            } else {
                //待续...
            }
        }
        return map;
    }

    //拼装json
    private static void getTypeParam(String type, ProcessContextV2 cxt, ServiceCaller serviceCaller) {
        if (type.equals(JSON_PARAM)) {
            JSONObject jsonObject = new JSONObject();
            JsonParam jsonParam = serviceCaller.getJsonParam();
            String name = jsonParam.getName();

            List<Mapping> mpList = jsonParam.getMappings();
            for (Mapping mapping : mpList) {
                String typ = mapping.getType();
                if (StringUtils.isNotBlank(typ) && typ.equals(ARRAY_PARAM)) {
                    JSONArray array = new JSONArray();
                    ArrayParam arrayParam = serviceCaller.getArrayParam();
                    List<Jsons> jsons = arrayParam.getJsons();
                    for (Jsons json : jsons) {
                        JSONObject object = new JSONObject();
                        List<Mapping> mjList = json.getMappings();
                        for (Mapping mapp : mjList) {
                            String source = mapp.getSource();
                            String val = VariableUtil.parse(source, cxt);
                            object.put(mapp.getTarget(), val);
                        }
                        array.add(object);

                    }
                    cxt.put(arrayParam.getName(), array);
                }


                String source = mapping.getSource();
                String val = VariableUtil.parse(source, cxt);
                jsonObject.put(mapping.getTarget(), val);
            }
            cxt.put(name, jsonObject.toString());
        } else if (type.equals(ARRAY_PARAM)) {
            JSONArray array = new JSONArray();
            ArrayParam arrayParam = serviceCaller.getArrayParam();
            List<Jsons> jsons = arrayParam.getJsons();
            for (Jsons json : jsons) {
                JSONObject object = new JSONObject();
                List<Mapping> mjList = json.getMappings();
                for (Mapping mapp : mjList) {
                    String source = mapp.getSource();
                    String val = VariableUtil.parse(source, cxt);
                    object.put(mapp.getTarget(), val);
                }
                array.add(object);

            }
            cxt.put(arrayParam.getName(), array);
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ServiceCallingManager getServiceCallingManager() {
        return serviceCallingManager;
    }

    public void setServiceCallingManager(ServiceCallingManager serviceCallingManager) {
        this.serviceCallingManager = serviceCallingManager;
    }


    public static void main(String[] args) {
//        HttpHandler handler = new HttpHandler();
//        handler.setType("http");
//        ServiceCallingManager scm = new ServiceCallingManager();
//        AttemptManager am = new AttemptManager();
//        am.setIntervalInMili(300);
//        am.setTimes(5);
//        scm.setCallingManager(am);
//        ServiceCaller sc = new ServiceCaller();
//        sc.setFailurePolicy("attemp");
//        sc.setMethod("GET");
//        sc.setParamForm("map");
//        sc.setTimeThreshold(30);
//        sc.setUrl("http://localhost:8080/dp/v1/handle");
//        Mapper mapper = new Mapper();
//        Mapping mapping = new Mapping();
//        mapping.setSource("111");
//        mapping.setTarget("gid");
//        mapper.setMappings(new ArrayList<Mapping>() {{
//            add(mapping);
//        }});
//        sc.setMapper(mapper);
//        scm.setServiceCaller(sc);
//        handler.setServiceCallingManager(scm);
//
//        ProcessContext pc = new ProcessContext();
//        pc.put(ProcessContextEnum.GID, "111");
//        handler.handler(pc);

//        Map<String,List<String>> map = Maps.newHashMap();
//        List<String> list = new ArrayList<String>(){{
//            add("a");
//            add("a");
//            add("a");
//            add("a");
//            add("a");
//            add("a");
//            add("a");
//        }};
//        List<String> list2 = new ArrayList<String>(){{
//            add("b");
//            add("b");
//            add("b");
//        }};
//        map.put("list",list);
//        map.put("list2",list);
//        int count = count(map);
//        System.out.println(count);

        String sss = "00002";
        System.out.println(Integer.parseInt(sss));

    }
}
