package com.tcredit.engine.engine;

import com.google.common.collect.Maps;
import com.tcredit.engine.conf.ConfigManagerV2;
import com.tcredit.engine.conf.Params;
import com.tcredit.engine.conf.Period;
import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.handler.Handler;
import com.tcredit.engine.conf.handler.httpHandler.HttpHandler;
import com.tcredit.engine.conf.handler.httpHandler.ServiceCaller;
import com.tcredit.engine.conf.handler.httpHandler.ServiceCallingManager;
import com.tcredit.engine.conf.reader.Reader;
import com.tcredit.engine.conf.writer.Writer;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.data_process.DataStorage;
import com.tcredit.engine.data_process.hbaseDataProcessUtil.HBaseDataProcessUtil;
import com.tcredit.engine.data_process.mongoDataProcessUtil.MongoDataProcessUtil;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.util.DateUtil;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.MD5_HMC_EncryptUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-08 18:24
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-08 18:24
 * @updatedRemark:
 * @version:
 */
public class FutureStepExcutor implements Callable<String> {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(FutureStepExcutor.class);


    private Step step;
    private ProcessContextV2 cxt;

    public FutureStepExcutor(Step step, ProcessContextV2 cxt) {
        this.cxt = cxt;
        this.step = step;
    }

    @Override
    public String call() {
        boolean addOk = cxt.addStep2BeProcessStepIds(step.getId());

        if (addOk) {
            ResponseData response = null;
            String originContent = null;
            Period period = step.getPeriod();
            String rid = checkDataPeriod(cxt.get(ProcessContextEnum.GID).toString(), period, cxt);
//                LOGGER.info(step.getId() + "---------------------------------------------" + rid);
            if (StringUtils.isBlank(rid)) {

                //将不在有效期内的stepid放入上下文
                cxt.getNoValidStepId().add(step.getId());


                /**  执行reader  **/
                Reader reader = step.getReader();
                try {
                    //reader.read(cxt);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                /** 执行hander  **/
                Handler handler = step.getHandler();
                try {
                    if (handler != null) {
                        originContent = handler.handler(cxt, step);
                        response = JsonUtil.json2Object(originContent, ResponseData.class);
                        String dataRid = response.getRid();
                        /**
                         * 更新数据报告id到有效期管理表
                         */
                        updateDataPeriodForDataSource(cxt.get(ProcessContextEnum.GID).toString(), period, dataRid);
                    }
                } catch (Exception e) {
                    /** 是否需要终止流程 **/
                    endOverallProcessing(handler, cxt, e);
                }

                /**  执行writer  **/
                Writer writer = step.getWriter();

                try {
                    if (writer != null) {
                        LOGGER.info("gid:" + cxt.get(ProcessContextEnum.GID) + ",mid:" + step.getId() + ",writer不为空");
                        writer.write(cxt, step, response);

                    }
                } catch (Exception e) {
                    LOGGER.error("gid:{},step:{},writer处理异常，异常信息:{}", cxt.get(ProcessContextEnum.GID), step.getId(), e);

                }

                //处理中间过程相关结果放入上下文
                putRltToCxt(cxt, step, originContent);


                /**  是否继续数据流程
                 * results是否配置，返回层是否执行完成**/
                if (!cxt.isExceptionFlag()) {
                    //here 将执行完的stepId放入上下文中
                    cxt.getFinishedStepIds().add(step.getId());
                    if (ProcessEngine.canStartNextStep(cxt, step.getId())) {
                        ProcessEngine.fetchNextStepAndExcute(step.getId(), cxt);
                    }
                }

            } else {
//                    LOGGER.info("==>step:{},数据报告id:{}", step.getId(), rid);
                cxt.put(step.getId(), rid);
                //here 将执行完的stepId放入上下文中
                cxt.getFinishedStepIds().add(step.getId());

                /**  是否继续数据流程
                 * results是否配置，返回层是否执行完成**/
                if (ProcessEngine.canStartNextStep(cxt, step.getId())) {
                    ProcessEngine.fetchNextStepAndExcute(step.getId(), cxt);
                }
            }


        }
        return step.getId();
    }

    //处理中间过程相关结果放入上下文
    private void putRltToCxt(ProcessContextV2 cxt, Step step, String originContent) {
        List<String> fields = step.getField();
        if (fields.size() != 0 && originContent != null && StringUtils.isNotBlank(originContent)) {
            Map<String, Object> map = JsonUtil.json2Object(originContent, Map.class);
            Map<String, Object> result = Maps.newHashMap();
            for (String field : fields) {
                Object obj = map.get(field);
                if (obj != null) {
                    result.put(field, obj);
                }
            }
            cxt.getStoreResults().put(step.getId(), result);
        }
    }


    /** 是否需要终止流程 **/
    private void endOverallProcessing(Handler handler, ProcessContextV2 cxt, Exception e) {
        try {

            ServiceCallingManager serviceCallingManager = handler.getServiceCallingManager();
            if (serviceCallingManager != null &&
                    serviceCallingManager.getServiceCaller() != null &&
                    serviceCallingManager.getServiceCaller().getFailurePolicy() != null) {
                ServiceCaller serviceCaller = serviceCallingManager.getServiceCaller();
                //如果请求失败，后续策略选择
                String failurePolicy = serviceCaller.getFailurePolicy();

                if (HttpHandler.REQ_FAIL_ATTEMPT.equals(failurePolicy)) {
                    LOGGER.error("gid:{},step:{},handler处理异常，异常信息:{}", cxt.get(ProcessContextEnum.GID), step.getId(), e);
                    cxt.setExceptionFlag(true);
                    //错误信息放入上下文
                    cxt.put(ProcessContextEnum.ERROR, e.getMessage());
                } else if (HttpHandler.REQ_FAIL_IGNORE.equals(failurePolicy)) {
                    LOGGER.warn("gid:{},step:{},handler处理异常,但继续执行，异常信息:{}", cxt.get(ProcessContextEnum.GID), step.getId(), e);
                }
            }


        } catch (Exception ex) {
            LOGGER.error("gid:{},step:{},handler判断是否终止数据流程异常，异常信息:{}", cxt.get(ProcessContextEnum.GID), step.getId(), ex);
        }


    }


    /**
     * 更新数据有限期到有效期表
     *
     * @param gid
     * @param period
     * @param dataRid
     */
    private void updateDataPeriodForDataSource(String gid, Period period, String dataRid) {
        if (period == null) return;
        String mainTable = period.getMainTable();
        Map<String, String> paramKVs = period.getParams().getParams();
        if (StringUtils.isNotBlank(mainTable) && mainTable.equalsIgnoreCase(DataStorage.HBASE_3d_DS_PERIOD_TB)) {
            try {
                /**
                 * 生成rowKey
                 */
                String uuid = paramKVs.get(Period.UUID);
                String tid = paramKVs.get(Period.TID);
                String rowkeyBase = StringUtils.isNotBlank(uuid) ? uuid : tid;

                String rowKey = HBaseDataProcessUtil.generateRowKey(gid, rowkeyBase, period.getDataSource());
                if (StringUtils.isNotBlank(rowKey)) {
                    Map<String, Object> map = HBaseDataProcessUtil.queryDataByRowKey(gid, rowKey, DataStorage.HBASE_3d_DS_PERIOD_TB);
                    if (map != null) {
                        map.put(Period.TIME_UPD, DateUtil.formatDate2StrFromDate(new Date(), DateUtil.DATE_FORMAT_yMdHms));
                        map.put(Period.RID, dataRid);
                        HBaseDataProcessUtil.storeData(gid, rowKey, map);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("gid:{},更新数据源:{},有效期表出错，错误信息:{}", gid, period.getDataSource(), e);
            }
        }

    }

    /**
     * 检测数据有效期
     *
     * @param period
     * @param cxt
     * @return 在有效内返回报告id
     */
    private String checkDataPeriod(String gid, Period period, ProcessContextV2 cxt) {
        if (period == null) {
            return null;
        }
        String mainTable = period.getMainTable();
        Params param = period.getParams();
        param.parseParam(cxt);
        Map<String, String> paramKVs = param.getParams();


        /**
         * 检测
         */
        String rid = null;
        if (StringUtils.isNotBlank(mainTable)) {
            try {

                //mongo
                List<String> mongoTableList = ConfigManagerV2.getDbType().get(ConfigManagerV2.MONGO);
                if (mongoTableList.contains(mainTable)) {
                    Bson filterOne = and(eq("_id", gid));
                    long currentTimeMillis = System.currentTimeMillis();//当前时间毫秒
                    Document getResult = MongoDataProcessUtil.get(MongoDataProcessUtil.APPLICATION_DB, mainTable, filterOne);
                    if (getResult != null) {
                        Map<String, String> map = JsonUtil.json2Object(getResult.toJson(), Map.class);
                        long lastUpdTime = DateUtil.parseString2Date(map.get("tm_isrt").toString(), DateUtil.DATE_FORMAT_yMdHms).getTime();
                        if (currentTimeMillis - lastUpdTime <= period.getPeriodInSecond() * 1000) {
                            long endTimeMillis = System.currentTimeMillis();//当前时间毫秒
                            LOGGER.info("gid:" + gid + ",查询表:" + mainTable + ",用时:" + (endTimeMillis - currentTimeMillis) + "ms");
                            return gid;
                        }
                    }
                }
                //mongo


                if (mainTable.equalsIgnoreCase(DataStorage.HBASE_3d_DS_PERIOD_TB)) {
                    /**
                     * 生成rowKey
                     */
                    String uuid = paramKVs.get(Period.UUID);
                    String tid = paramKVs.get(Period.TID);
                    String rowkeyBase = StringUtils.isNotBlank(uuid) ? uuid : tid;

                    String rowKey = HBaseDataProcessUtil.generateRowKey(gid, rowkeyBase, period.getDataSource());
                    if (StringUtils.isNotBlank(rowKey)) {
                        Map<String, Object> map = HBaseDataProcessUtil.queryDataByRowKey(gid, rowKey, DataStorage.HBASE_3d_DS_PERIOD_TB);
                        if (map != null && map.get(Period.TIME_UPD) != null) {
                            Object o = map.get(Period.TIME_UPD);
                            long lastUpdTime = DateUtil.parseString2Date(o.toString(), DateUtil.DATE_FORMAT_yMdHms).getTime();
                            long currentTimeMillis = System.currentTimeMillis();
                            /**
                             * 数据在有效期
                             */
                            if (currentTimeMillis - lastUpdTime <= period.getPeriodInSecond() * 1000) {
                                Object ridObj = map.get(Period.RID);
                                if (ridObj != null && StringUtils.isNotBlank(ridObj.toString())) {
                                    rid = ridObj.toString();
                                }
                            }
                        } else if (map == null || map.isEmpty()) {
                            if (map == null) {
                                map = new HashMap<>();
                            }

                            map.put(Period.REFER_KEY, rowkeyBase);
                            map.put(Period.RID, null);
                            map.put(period.TIME_INST, DateUtil.formatDate2StrFromDate(new Date(), DateUtil.DATE_FORMAT_yMdHms));
                            map.put(period.TIME_UPD, DateUtil.formatDate2StrFromDate(new Date(), DateUtil.DATE_FORMAT_yMdHms));
                            map.put(Period.DATA_SOURCE, period.getDataSource());
                            HBaseDataProcessUtil.storeData(gid, rowKey, map);
                        }
                    }
                } else if (DataStorage.MODEL_STEP_TABLE_NAME.equalsIgnoreCase(mainTable)) {
                    String s = paramKVs.get(Period.GID);
                    String modelID = paramKVs.get(Period.MODEL_ID);
                    String md5SourceStr = s + DataStorage.SPLIT_LINE_MID + modelID;
                    String rowKeyBase = MD5_HMC_EncryptUtils.getMd5(md5SourceStr, 1);
                    if (StringUtils.isNotBlank(rowKeyBase)) {
                        List<Map<String, Object>> maps = HBaseDataProcessUtil.queryDataByRowKey(rowKeyBase, mainTable);
                        if (maps != null && !maps.isEmpty()) {
                            rid = paramKVs.get(Period.GID);
                        }
                    }

                } else {
                    List<Map<String, Object>> maps = HBaseDataProcessUtil.queryDataByRowKey(paramKVs.get(Period.GID), mainTable);
                    if (maps != null && !maps.isEmpty()) {
                        rid = paramKVs.get(Period.GID);
                    }

                }

            } catch (Exception e) {
                LOGGER.error("gid:{},判断有效期异常,异常信息:{}", gid, e);
            }
        }
        return rid;
    }
}
