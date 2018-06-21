package com.tcredit.engine.processService.impl;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tcredit.engine.constants.HttpConstant;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextHolder;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.data_process.orientDataProcessUtil.OrientDataProcessUtil;
import com.tcredit.engine.engine.ProcessEngine;
import com.tcredit.engine.exception.ParamException;
import com.tcredit.engine.exception.UUIDException;
import com.tcredit.engine.processService.DataProcessingService;
import com.tcredit.engine.processService.DataRetrieveService;
import com.tcredit.engine.processService.DataService;
import com.tcredit.engine.response.DataResponse;
import com.tcredit.engine.response.OutResponse;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.DateUtil;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PropertiesUtil;
import com.tcredit.engine.util.httpClient.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DataServiceImpl implements DataService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataServiceImpl.class);
    private static final String ID_card = "idcard";
    private static final String ID_CARD = "idCard";
    private static final String ORIENTDB_INSERT_DATA_DBNAME = "rc_applyform";

    @Autowired
    DataProcessingService dataProcessingService;

    @Resource(name = "dataRetrieveServiceImpl")
    DataRetrieveService dataRetrieveService;


    @Override
    public DataResponse getDataProd(HttpServletRequest request) {
        LOGGER.info("数据产品开始——————————————");
        long startTime = System.currentTimeMillis();
        ProcessContextHolder.setDataHandlerSynchronized(true);


        DataResponse dataResponse = new DataResponse();

        //入参
        Map<String, Object> params = null;
        String gid = null;
        String seq_num = null;
        String mid = null;

        try {
            params = getParamFromRequest(request);
            LOGGER.info("数据产品入参为:" + JsonUtil.toJson(params));
            mid = String.valueOf(params.get("prod_code"));
            gid = String.valueOf(params.get(ProcessContextEnum.GID.val));
            seq_num = String.valueOf(params.get("seq_num"));
            boolean chkParam = chkParam(params);
            if (chkParam) {


                params.put("uuid", uuidService(params));
                params.put("mid", mid);
                params.put("dateBack", DateUtil.nowDate());

                /**
                 * 申请单入库rc_applyform
                 * 入库失败不影响主流程
                 */
                try {
                    new OrientDBInsertDataPordParamThread(params).start();
                } catch (Exception e) {
                    LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",申请单入库异常，异常信息" + e.getMessage());
                }


                /**
                 * 开启数据处理
                 */

                LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",参数效验完成，开始处理——————");

                //创建数据处理过程上下文
                ProcessContextV2 cxt = dataProcessingService.buildContext(params);
                OutResponse response = JsonUtil.json2Object(ProcessEngine.start(cxt), OutResponse.class);
                if (response.getCode().equals("0")) {
                    ResponseData responseData = (ResponseData) cxt.getStepWriteMap().get(mid + "_res");
                    dataResponse.setGid(gid);
                    return getResult(responseData, cxt, seq_num);
                } else {
                    //处理异常，获取上下文中的错误信息
                    String e = String.valueOf(cxt.get(ProcessContextEnum.ERROR));
                    LOGGER.error("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + "参数错误:" + e);
                    dataResponse.setMessage(e);
                    dataResponse.setGid(gid);
                    return dataResponse;
                }
            }
        } catch (ParamException e) {
            LOGGER.error("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + "参数错误:" + e.getMessage());
            //没有获取到参数param、参数错误
            dataResponse.setMessage(e.getMessage());
            dataResponse.setStatus("-1");
            dataResponse.setGid(gid);
            return dataResponse;

        } catch (UUIDException e) {
            LOGGER.error("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",参数:" + params + ",UUID生成失败:" + e.getMessage());
            //UUID生成失败
            dataResponse.setMessage(e.getMessage());
            dataResponse.setGid(gid);
            return dataResponse;
        } catch (Exception e) {
            //其他异常
            LOGGER.error("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + "数据产品异常:", e.getMessage());
            dataResponse.setGid(gid);
            dataResponse.setMessage("数据产品异常");
            return dataResponse;
        } finally {
            long endTime = System.currentTimeMillis();
            LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + "数据产品处理完毕,耗时" + (endTime - startTime) + "ms");
        }
        dataResponse.setGid(gid);
        return dataResponse;
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

            /**适配产品迁移**/

            Object idCard = map.get(ID_CARD) == null ? map.get(ID_card) : map.get(ID_CARD);
            map.put(ID_card, idCard);


            return map;
        } catch (Exception e) {
            LOGGER.error("参数格式错误————退出，错误信息：{}", e);
            throw new ParamException("参数解析错误,请检查参数格式");
        }


    }

    private boolean chkParam(Map<String, Object> requestMap) throws ParamException {
        String gid = String.valueOf(requestMap.get(ProcessContextEnum.GID.val));

        if (requestMap == null) {
            throw new ParamException("参数param为空");
        }
        if (requestMap.get("prod_code") == null || StringUtils.isBlank(requestMap.get("prod_code").toString())) {
            throw new ParamException("缺少参数prod_code");
        }
        String mid = requestMap.get("prod_code").toString();

        String requireds = PropertiesUtil.getDataProdMap().get(mid + ".required");

        if (requireds != null) {
            String[] required = requireds.split(",");
            for (String str : required) {
                if (requestMap.get(str) == null || StringUtils.isBlank(requestMap.get(str).toString())) {
                    throw new ParamException("缺少参数" + str);
                }
            }
        } else {
            throw new ParamException("参数错误，没有匹配到相应模块" + mid);
        }


        return true;
    }

    private String uuidService(Map<String, Object> params) {
        String mid = String.valueOf(params.get("prod_code"));
        String gid = String.valueOf(params.get(ProcessContextEnum.GID.val));
        String seq_num = String.valueOf(params.get("seq_num"));

        long startTime = System.currentTimeMillis();
        String uuidUrl = PropertiesUtil.getServiceMap().get("uuid_service");
        Map<String, String> ma = new HashMap<>();
        ma.put("name", String.valueOf(params.get("name")));

        ma.put("cardNo", String.valueOf(params.get("idcard")));

        LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + "访问UUID:" + uuidUrl + ",参数:" + JsonUtil.toJson(ma));

        String reContent = HttpClientUtil.httpPost(uuidUrl, ma, 30 * 1000);

        LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + "访问UUID:" + uuidUrl + ",返回结果:" + reContent);

        HashMap uMap = JsonUtil.json2Object(reContent, HashMap.class);

        if (uMap == null) {
            throw new UUIDException("UUID生成失败");
        }
        if (!uMap.get("code").equals("200")) {
            throw new UUIDException("UUID生成失败," + uMap.get("message"));
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + "访问UUID成功，耗时:" + (endTime - startTime) + "ms");
        return String.valueOf(uMap.get("uniqueId"));
    }


    private DataResponse getResult(ResponseData responseData, ProcessContextV2 cxt, String seq_num) {
        DataResponse dataResponse = new DataResponse();
        String mid = String.valueOf(cxt.get(ProcessContextEnum.MODULE_ID));
        String gid = String.valueOf(cxt.get(ProcessContextEnum.GID));
        String moduleId = PropertiesUtil.getDataProdMap().get(mid + ".moduleId");

        if (responseData != null) {
            List<TableData> tableDatas = responseData.getData();
            TableData tableData = tableDatas.get(0);
            Map<String, Object> map = tableData.getData().get(0);

            dataResponse.setGid(gid);
            dataResponse.setMessage("成功");
            dataResponse.setModuleId(moduleId);
            dataResponse.setStatus("0");
            dataResponse.setIsFee(getFee(map, mid, seq_num));
            dataResponse.setData(getMap(map, mid, seq_num));
        } else {
            LOGGER.error("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",未获取到处理结果,退出——————");
            dataResponse.setGid(gid);
            return dataResponse;
        }


        return dataResponse;
    }

    private String getFee(Map<String, Object> map, String mid, String seq_num) {
        String gid = String.valueOf(map.get(ProcessContextEnum.GID.val));
        String isFee = null;
        try {
            isFee = PropertiesUtil.getDataProdMap().get(mid + ".isFee");
            if (isFee != null) {
                LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",isFee:" + isFee + ",判断计费---");
                int strStartIndex = isFee.indexOf(":");

                String key = isFee.substring(0, strStartIndex);
                //不扣费的值
                String val = isFee.substring(strStartIndex + 1, isFee.length());

                //结果实际返回值
                String keyVal = String.valueOf(map.get(key));
                if (val.equals(keyVal)) {
                    return "-1";
                } else {
                    return "0";
                }
            } else {
                LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",isFee:" + isFee + ",判断计费方式为空——————");
                return "0";
            }
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",isFee:" + isFee + ",判断计费异常，异常信息:", e);

        }
        return "-1";
    }

    private static Map<String, Object> getMap(Map<String, Object> map, String mid, String seq_num) {
        String gid = String.valueOf(map.get(ProcessContextEnum.GID.val));

        //获取要剔除的字段
        String filter = PropertiesUtil.getDataProdMap().get(mid + ".filter");
        String[] ridStr = filter.split(",");
        for (String str : ridStr) {
            map.remove(str);
        }
        LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",剔除返回结果中的:" + filter);
        return map;
    }


    public class OrientDBInsertDataPordParamThread extends Thread {
        private Map<String, Object> params;

        public OrientDBInsertDataPordParamThread(Map<String, Object> params) {
            this.params = params;
        }


        @Override
        public void run() {
            String mid = String.valueOf(params.get("prod_code"));
            String gid = String.valueOf(params.get(ProcessContextEnum.GID.val));
            String seq_num = String.valueOf(params.get("seq_num"));

            LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",申请单信息准准备入库————");

            ODatabaseDocumentTx databaseDocument = OrientDataProcessUtil.getFactory(OrientDataProcessUtil.RC_APPLYFORM).acquire();
            ODocument entries = new ODocument(ORIENTDB_INSERT_DATA_DBNAME);
            entries.fromJSON(JsonUtil.toJson(params));
            entries.field("date_inst", new SimpleDateFormat(DateUtil.DATE_FORMAT).format(new Date()));
            entries.field("time_inst", String.valueOf(System.currentTimeMillis()));
            entries.save();
            databaseDocument.close();

            LOGGER.info("gid:" + gid + ",seq_num:" + seq_num + ",mid:" + mid + ",申请单信息入库成功————");


        }
    }

}
