package com.tcredit.engine.data_process;

import com.tcredit.engine.conf.ConfigManagerV2;
import com.tcredit.engine.conf.Step;
import com.tcredit.engine.dbEntity.AntifraudRetrieveEntity;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.MD5_HMC_EncryptUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-12 17:10:23
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-12 17:10:23
 * @updatedRemark:
 * @version:
 */
public class DataProcessor {

    /**
     * 数据入库
     *
     * @param gid
     * @param rid
     * @param step
     * @param datas
     * @param otherData
     * @throws Exception
     */

    public static void storage(String gid, String rid, String step, TableData datas, Map<String, Object> otherData, Step s) throws Exception {
        /**
         *
         *插入到数据库里
         */

        if (s != null) {
            DataStorage dataStorage = null;
            if (s.getStoreType() != null) {
                if (s.getStoreType().equals(ConfigManagerV2.MONGO)) {
                    dataStorage = DataOperationFactory.getDataStorage(DataOperationFactory.MONGO);
                }
            } else {
                dataStorage = DataOperationFactory.getDataStorage(DataOperationFactory.HBASE);
            }


            if (dataStorage != null && datas != null && datas.getData() != null && !datas.getData().isEmpty()) {
                String rowKeyBase = null;
                String tblName = (step + DataStorage.SPLIT_LINE + datas.getDbName() + DataStorage.SPLIT_LINE + datas.getTableName()).toLowerCase();
                if (DataStorage.MODEL_STEP_TABLE_NAME.equalsIgnoreCase(tblName)) {
                    Object o = datas.getData().get(0).get(DataStorage.MODEL_ID_SIGN);
                    if (o != null) {
                        String md5SourceStr = rid + DataStorage.SPLIT_LINE_MID + o.toString();
                        rowKeyBase = MD5_HMC_EncryptUtils.getMd5(md5SourceStr, 1);
                    }
                } else {
                    rowKeyBase = rid;
                }
                dataStorage.storage(gid, rowKeyBase, step, datas, otherData);
            }


        } else {
            throw new RuntimeException("dbType错误，未能获取有效的存储实体");
        }

    }

    /**
     * 检索数据，根据gid
     *
     * @param entity
     * @return
     */
    public static List<Map<String, Object>> retrieve(RetrieveEntity entity) {
        String tbName = null;
        if (StringUtils.isNotBlank(entity.step) && StringUtils.isNotBlank(entity.db)) {
            tbName = entity.step + "_" + entity.db + "_" + entity.tblName;
        } else {
            tbName = entity.tblName;
        }
        List<String> list = ConfigManagerV2.getDbType().get(ConfigManagerV2.MONGO);
        if (list.contains(tbName.toLowerCase())) {
            return DataOperationFactory.getDataRetrieve(DataOperationFactory.MONGO).retrieve(entity);
        }

        return DataOperationFactory.getDataRetrieve(DataOperationFactory.HBASE).retrieve(entity);
    }

    /**
     * 检索数据，根据gid
     *
     * @param entity
     * @return
     */
    @Deprecated
    public static List<Map<String, Object>> antifraudVariableRetrieveByMysql(AntifraudRetrieveEntity entity) {
        return DataOperationFactory.getDataRetrieve(DataOperationFactory.ANTIFRAUD_MYSQL).retrieve(entity);
    }

    /**
     * 检索数据，根据gid
     *
     * @param entity
     * @return
     */
    public static List<Map<String, Object>> antifraudVariableRetrieveByHBase(AntifraudRetrieveEntity entity) {
        return DataOperationFactory.getDataRetrieve(DataOperationFactory.ANTIFRAUD_HBASE).retrieve(entity);
    }

    public static void main(String[] args) {
        String dateFormat = "date(yyyy-MM-dd HH:mm:SS)";
        System.out.println(dateFormat.substring(dateFormat.indexOf('(') + 1, dateFormat.indexOf(')')));
    }

}
