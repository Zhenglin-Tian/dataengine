package com.tcredit.engine.data_process;

import com.google.common.collect.Lists;
import com.tcredit.engine.data_process.hbaseDataProcessUtil.HBaseDataProcessUtil;
import com.tcredit.engine.response.TableData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-03 17:36
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-03 17:36
 * @updatedRemark:
 * @version:
 */
public class DataStorageHbase implements DataStorage {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataStorageHbase.class);

    private static List<String> HBASE_TABLES = Lists.newArrayList();

    @Override
    public void storage(String gid, String rowKeyBase, String step, TableData tableData, Map<String, Object> otherData) throws Exception {
        /**
         * 在hbase中创建表,表名:数据处理阶段_DBName_tableName 如：std_std_antifraudvariable 全部小写
         */
        String tblName = (step + SPLIT_LINE + tableData.getDbName() + SPLIT_LINE + tableData.getTableName()).toLowerCase();
        String[] fs = {HBASE_DEFAULT_FAMILY_NAME};
        try {
            if (!HBASE_TABLES.contains(tblName)) {
                HBASE_TABLES.add(tblName);
                HBaseDataProcessUtil.createTable(tblName, fs);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("gid:%s,数据处理阶段:%s,数据入库过程建表异常，异常信息:%s", gid, step, e));
            /**
             * 连接异常退出
             */
            throw new RuntimeException("hbase建表异常，异常信息:" + e.getMessage());
        }
        /**
         * 将数据存入hbase
         */
        try {

            HBaseDataProcessUtil.storeTableData(tblName, HBASE_DEFAULT_FAMILY_NAME, rowKeyBase, tableData, otherData);

        } catch (IOException e) {
            LOGGER.error(String.format("gid:%s,数据处理阶段:%s,数据入库过程异常，异常信息:%s", gid, step, e));
            throw new RuntimeException("数据入库异常，异常信息:" + e.getMessage());
        }
    }


    public static List<String> getHbaseTables() {
        return HBASE_TABLES;
    }

    public static void setHbaseTables(List<String> hbaseTables) {
        HBASE_TABLES = hbaseTables;
    }
}
