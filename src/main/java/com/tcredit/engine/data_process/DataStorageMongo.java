package com.tcredit.engine.data_process;

import com.mongodb.MongoWriteException;
import com.tcredit.engine.conf.ConfigManagerV2;
import com.tcredit.engine.data_process.mongoDataProcessUtil.MongoDataProcessUtil;
import com.tcredit.engine.response.TableData;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.*;

import java.util.List;
import java.util.Map;


public class DataStorageMongo implements DataStorage {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataStorageMongo.class);


    @Override
    public void storage(String gid, String rid, String step, TableData tableData, Map<String, Object> otherData) throws Exception {
        String tbName = null;
        try {
            long startTime = System.currentTimeMillis();//当前时间毫秒
            tbName = (step + "_" + tableData.getDbName() + "_" + tableData.getTableName()).toLowerCase();
            List<String> columns = RecordColumnsUtil.getRecordColumns(tableData);
            for (Map<String, Object> data : tableData.getData()) {
                Document document = new Document();
                for (String column : columns) {
                    Object val = data.get(column);
                    document.put(column, String.valueOf(val));

                }
                List<String> list = ConfigManagerV2.getDbType().get(ConfigManagerV2.MONGO);

                if (!list.contains(tbName)) {
                    ConfigManagerV2.getDbType().get(ConfigManagerV2.MONGO).add(tbName);
                }

                document.put("_id", rid);
                try {
                    MongoDataProcessUtil.put(MongoDataProcessUtil.APPLICATION_DB, tbName, document);
                    long endTime = System.currentTimeMillis();//当前时间毫秒
                    LOGGER.info("gid:" + gid + ",rid:" + rid + ",tbName:" + tbName + ",数据插入成功,耗时:" + (endTime - startTime) + "ms");
                } catch (MongoWriteException e) {
                    LOGGER.info("gid:" + gid + ",rid:" + rid + ",tbName:" + tbName + ",数据存在,执行更新操作");
                    Bson filterOne = and(eq("_id", rid));
                    MongoDataProcessUtil.update(MongoDataProcessUtil.APPLICATION_DB, tbName, filterOne, document);
                    long endTime = System.currentTimeMillis();//当前时间毫秒
                    LOGGER.info("gid:" + gid + ",rid:" + rid + ",tbName:" + tbName + ",数据更新成功,耗时:" + (endTime - startTime) + "ms");
                }
            }
        } catch (Exception e) {
            LOGGER.error("gid:" + rid + ",tbName:" + tbName + ",数据入库失败,异常信息:", e);
            throw new RuntimeException("mongo入库异常");

        }


    }


}



