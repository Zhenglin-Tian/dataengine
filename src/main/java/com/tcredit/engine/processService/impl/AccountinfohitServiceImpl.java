package com.tcredit.engine.processService.impl;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tcredit.engine.data_process.hbaseDataProcessUtil.HBaseDataProcessUtil;
import com.tcredit.engine.data_process.mongoDataProcessUtil.MongoDataProcessUtil;
import com.tcredit.engine.data_process.orientDataProcessUtil.OrientDataProcessUtil;
import com.tcredit.engine.exception.ParamException;
import com.tcredit.engine.processService.AccountinfohitService;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.util.FileUtil;
import com.tcredit.engine.util.PropertiesUtil;
import com.tcredit.engine.util.ReadCSVUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class AccountinfohitServiceImpl implements AccountinfohitService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(AccountinfohitServiceImpl.class);
    public static final String URL_PATH_HBASE = PropertiesUtil.getString("ACCOUNT_PATH");
    public static final String URL_PATH_MONGO = PropertiesUtil.getString("URL_PATH_MONGO");
    public static final String TABLE_NAME = "std_std_ip_beesmell_regcanal_result";
    public static final String FAMILY_NAME = "default_column_family";
    public static final String MOBILE = "mobile";
    public static final String ROW_KEY = "rowKey";
    public static final String TEL = "tel";
    public static final String ONLINE_REPORT_ID = "online_report_id";

    @Override
    public BaseResponse insDataMongo() {
        long starTime = System.currentTimeMillis();//当前时间毫秒
        long time = 0;
        BaseResponse baseResponse = new BaseResponse();
        try {
            File[] files = FileUtil.listFiles(URL_PATH_HBASE);


            for (File file : files) {
                List<Document> list = ReadCSVUtil.readFileReList(file);
                MongoDataProcessUtil.putList(MongoDataProcessUtil.APPLICATION_DB, TABLE_NAME, list);
            }
            long endTime = System.currentTimeMillis();//当前时间毫秒
            time = (endTime - starTime);
            baseResponse.setMessage("成功，耗时:" + time + "ms");

        } catch (Exception e) {
            LOGGER.error("入库异常，异常信息:", e);
            baseResponse.setCode("-1");
            baseResponse.setMessage("失败," + e.getMessage());
        } finally {
            LOGGER.info("蜂嗅历史数据入库完毕，耗时:" + time + "ms");
        }

        return baseResponse;
    }

    @Override
    public BaseResponse<List<Map<String, Object>>> selectAccountinfohit(HttpServletRequest request) {
        BaseResponse<List<Map<String, Object>>> baseResponse = new BaseResponse<>();
        long startTime = System.currentTimeMillis();
        String mobile = null;
        try {
            mobile = getMobileRequest(request);

            List<Map<String, Object>> list = MongoDataProcessUtil.queryMapNoLim(null, mobile, TABLE_NAME);
            if (list.size() != 0) {
                baseResponse.setData(list);
            } else {
                baseResponse.setCode("-5");
                baseResponse.setMessage("无数据");
            }
        } catch (ParamException e) {
            baseResponse.setCode("-1");
            baseResponse.setMessage(e.getMessage());
            LOGGER.info("蜂嗅历史数据查询参数错误:" + e.getMessage());

        } catch (Exception e) {
            baseResponse.setCode("-4");
            baseResponse.setMessage(e.getMessage());
            LOGGER.error("蜂嗅历史数据查询异常,异常信息:", e);
        } finally {
            long endTime = System.currentTimeMillis();
            LOGGER.info("蜂嗅历史数据查询完成,耗时:" + (endTime - startTime) + "ms");
        }


        return baseResponse;
    }

    @Override
    public BaseResponse insDataHbase() {
        long startTime = System.currentTimeMillis();//当前时间毫秒
        long time = 0;
        BaseResponse baseResponse = new BaseResponse();
        /*ODatabaseDocument databaseDocument = OrientDataProcessUtil.getFactory().acquire();
        databaseDocument.command(new OCommandSQL("CREATE CLASS std_std_ip_beesmell_regcanal_result")).execute();
        databaseDocument.command(new OCommandSQL("CREATE PROPERTY std_std_ip_beesmell_regcanal_result.rowKey STRING")).execute();
        databaseDocument.command(new OCommandSQL("CREATE PROPERTY std_std_ip_beesmell_regcanal_result.tel STRING")).execute();
        databaseDocument.command(new OCommandSQL("CREATE INDEX tel ON std_std_ip_beesmell_regcanal_result (tel) NOTUNIQUE")).execute();
        databaseDocument.close();*/
        try {
            LOGGER.info("mongo数据迁移到阿里hbase，进入——————————");
            LOGGER.info("开始读取文件");
            File[] files = FileUtil.listFiles(URL_PATH_MONGO);

            for (File file : files) {
                List<Document> list = ReadCSVUtil.readFileReList(file);

                LOGGER.info("读取完毕，开始入库，长度：" + list.size());

                HBaseDataProcessUtil.insOLDMongoListFORHbase(list, FAMILY_NAME, TABLE_NAME);

                LOGGER.info("habse入库完毕，OrientDB入库---");

                OrientSaveData orientSaveData = new OrientSaveData();
                orientSaveData.list = list;
                Thread thread = new Thread(orientSaveData);
                thread.start();
            }

            long endTime = System.currentTimeMillis();//当前时间毫秒
            time = (endTime - startTime);
            baseResponse.setMessage("成功，耗时:" + time + "ms");

        } catch (Exception e) {
            LOGGER.error("入库异常，异常信息:", e);
            baseResponse.setCode("-1");
            baseResponse.setMessage("失败," + e.getMessage());
        } finally {
            LOGGER.info("蜂嗅历史数据查询完成,耗时:" + time + "ms");
        }


        return baseResponse;
    }

    @Override
    public BaseResponse<List<Map<String, Object>>> selectAccountinfohitForHbase(HttpServletRequest request) {
        BaseResponse<List<Map<String, Object>>> baseResponse = new BaseResponse<>();
        long startTime = System.currentTimeMillis();
        String mobile = null;
        try {
            mobile = getMobileRequest(request);

            List<String> rowKeyList = OrientDataProcessUtil.selectRowKeyListByTel(mobile);
            if (rowKeyList != null) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (String rowKey : rowKeyList) {
                    Map<String, Object> map = HBaseDataProcessUtil.queryDataByRowKey(null, rowKey, TABLE_NAME);
                    if (map.size() != 0) {
                        list.add(map);
                    }
                }
                if (list.size() != 0) {
                    baseResponse.setData(list);
                    return baseResponse;
                } else {
                    baseResponse.setCode("-5");
                    baseResponse.setMessage("无数据");
                    return baseResponse;
                }


            } else {
                baseResponse.setCode("-5");
                baseResponse.setMessage("无数据");
                return baseResponse;
            }

        } catch (ParamException e) {
            baseResponse.setCode("-1");
            baseResponse.setMessage(e.getMessage());
            LOGGER.info("蜂嗅历史数据查询参数错误:" + e.getMessage());

        } catch (Exception e) {
            baseResponse.setCode("-4");
            baseResponse.setMessage(e.getMessage());
            LOGGER.error("蜂嗅历史数据查询异常,异常信息:", e);
        } finally {
            long endTime = System.currentTimeMillis();
            LOGGER.info("蜂嗅历史数据查询完成,耗时:" + (endTime - startTime) + "ms");
        }


        return baseResponse;
    }

    class OrientSaveData implements Runnable {
        List<Document> list;

        @Override
        public void run() {
            ODatabaseDocument databaseDocument = OrientDataProcessUtil.getFactory(OrientDataProcessUtil.RC_DATAENGINE).acquire();
            for (Document document : list) {
                ODocument entries = new ODocument(TABLE_NAME);
                entries.field(ROW_KEY, document.get(ONLINE_REPORT_ID));
                entries.field(TEL, document.get(TEL));
                entries.save();
            }
            databaseDocument.close();
        }
    }


    public String getMobileRequest(HttpServletRequest request) throws ParamException {

        try {
            String mobile = request.getParameter(MOBILE);
            if (StringUtils.isBlank(mobile)) {
                LOGGER.error("参数格式错误————退出");
                throw new ParamException("参数为空");
            }

            return mobile;
        } catch (Exception e) {
            LOGGER.error("参数格式错误————退出，错误信息：{}", e);
            throw new ParamException("参数解析错误,请检查参数格式");
        }


    }
}
