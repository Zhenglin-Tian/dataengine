package com.tcredit.engine.data_process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tcredit.engine.constants.Constants;
import com.tcredit.engine.data_process.mysqlDataSource.DruidDataSourceUtil;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-12 17:10:23
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-12 17:10:23
 * @updatedRemark:
 * @version:
 */
@Deprecated
public class DataStorageMysql implements DataStorage {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataStorageMysql.class);
    private static String INSERT_INTO = "INSERT INTO ";
    private static String LEFT_PARENTHESES = "(";
    private static String RIGHT_PARENTHESES = ")";
    private static String VALUS = ") VALUES(";
    private static String SEPARATOR = ",";
    private static String PARAM = "?,";

    @Override
    public void storage(String gid,String rid, String step, TableData tableData,Map<String,Object> otherData) throws SQLException {
        if (!checkDB(step)) {
            LOGGER.error(String.format("step:%s，step参数错误", step));
            throw new RuntimeException(String.format("dbEntity:%s，step参数错误", step));
        }
        Connection connection = DruidDataSourceUtil.getDataSource(Constants.DB_NAME_SIGN).getConnection();
        PreparedStatement pstm = null;
        try {
            storage(connection, gid, step, tableData,otherData);
        } catch (Exception e) {
            LOGGER.error(String.format("gid:%s,step:%s,dbEntity:%s,tableName:%s,数据:%s，入库失败", gid, step, tableData.getDbName(), tableData.getTableName(), JsonUtil.toJson(tableData)));
            throw new RuntimeException(String.format("gid:%s,step:%s,dbEntity:%s,tableName:%s,数据入库失败，信息：%s", gid, step, tableData.getDbName(), tableData.getTableName(), e.getMessage()));
        } finally {
            if (pstm != null) pstm.close();
            if (connection != null) connection.close();
        }
    }


    private boolean checkDB(String db) {
        boolean flag = false;
        if (StringUtils.isNotBlank(db) && Constants.DBS.contains(db)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 一张表的数据入库，每张表都带着全量的元数据信息，如果没有值则为空
     *
     * @param connection
     * @param tableData
     * @throws Exception
     */
    private void storage(Connection connection, String gid, String step, TableData tableData,Map<String,Object> otherData) throws Exception {
        if (StringUtils.isBlank(step) || tableData == null || tableData.getData().isEmpty()) return;
        if (connection == null) throw new RuntimeException("数据入库Connection为空");
        /**
         * 数据条数
         */
        int batchSize = tableData.getData().size();
        /**
         * 存储表的所有字段
         */
        List<String> keys = RecordColumnsUtil.getRecordColumns(tableData);

        if (keys != null && !keys.isEmpty()) {
            /**
             * 获取预备sql
             */
            //拼装库表
            String tblName = step + "_" + tableData.getDbName() + "_" + tableData.getTableName();
            if (StringUtils.isNotBlank(tblName)) {
                String sql = getSql(tblName.toLowerCase(), keys);


                /**
                 * 填充数据
                 */
                PreparedStatement pstm = connection.prepareStatement(sql);

                connection.setAutoCommit(false);
                for (Map<String, Object> kv : tableData.getData()) {
                    int i = 1;
                    for (String key : keys) {
                        Object val = kv.get(key);
                        /**
                         * 如果key从表数据中为取到对应的值，
                         */
                        if (val == null){
                            val = otherData.get(key);
                        }
                        /**
                         * 如果val 为""则置为null
                         */
                        if (val != null && StringUtils.isBlank(val.toString())){
                            val = null;
                        }

                        pstm.setObject(i, val);
                        i++;
                    }
                    if (batchSize > 1) {
                        pstm.addBatch();
                    }

//                    if (i%100==0){
//                        pstm.executeBatch();
//                        connection.commit();
//                        pstm.clearBatch();
//                    }
                }
                if (batchSize > 1) {
                    pstm.executeBatch();
                } else {
                    pstm.executeUpdate();
                }
                connection.commit();

            } else {
                throw new RuntimeException("库表为空");
            }
        }
    }


    /**
     *
     */


    /**
     * 获取预备sql
     *
     * @param tableName
     * @param keys
     * @return
     */
    private String getSql(String tableName, List<String> keys) {
        StringBuilder sql = new StringBuilder();
        StringBuilder fields = new StringBuilder();
        StringBuilder params = new StringBuilder();
        sql.append(INSERT_INTO).append(tableName.toLowerCase()).append(LEFT_PARENTHESES);
        for (String key : keys) {
            fields.append(key.toLowerCase() + SEPARATOR);
            params.append(PARAM);
        }
        sql.append(fields.substring(0, fields.length() - 1)).append(VALUS).
                append(params.substring(0, params.length() - 1)).append(RIGHT_PARENTHESES);
        return sql.toString();
    }


    public static void main(String[] args) throws Exception {
        String configPath = Object.class.getResource("/").getPath() + "druid_data_source";
        File[] configFiles = new File(configPath).listFiles();
        for (File file : configFiles) {
            Properties config = new Properties();
            try {
                InputStream is = new FileInputStream(file.getAbsolutePath());
                config.load(is);
                DruidDataSourceUtil.createDruidDataSourceAndPutInCache(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        System.out.println("============");


////        String sql = "select * from counter";
//        String sql = "insert into dadbear_test(id,name,age,sex,address) values(?,?,?,?,?)";
//        Connection connection = DruidDataSourceUtil.getDataSource("test")
//                .getConnection();
//        connection.setAutoCommit(false);
//        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//        ResultSet resultSet;
//        for (int i = 0; i < 2; i++) {
//            preparedStatement.setLong(1, 0L);
//            preparedStatement.setString(2, "name" + i);
//            preparedStatement.setInt(3, i);
//            preparedStatement.setString(4, "sex" + i);
//            preparedStatement.setString(5, "address" + i);
//            preparedStatement.addBatch();
//        }
//        preparedStatement.executeBatch();
////        connection.commit();
//        resultSet = preparedStatement.getGeneratedKeys();
//        while (resultSet.next()) {
//            System.out.println(resultSet.getMetaData().getColumnName(1));
//            System.out.println(resultSet.getMetaData().getColumnType(1));
//            System.out.println(resultSet.getMetaData().getColumnClassName(1));
//            System.out.println(resultSet.getMetaData().getColumnDisplaySize(1));
//            System.out.println(resultSet.getMetaData().getColumnLabel(1));
//            System.out.println(resultSet.getMetaData().getCatalogName(1));
//            System.out.println(resultSet.getMetaData().getColumnCount());
//            System.out.println(resultSet.getString(1));
//        }
//        connection.commit();
//
//        preparedStatement.close();
//        connection.close();
        /*
            sql和params的生成
         */
//        DataObjectHolder dataObjectHolder = new DataObjectHolder();
//        for (int i = 0; i < 10; i++) {
//            DataFieldHolder dataFieldHolder = new DataFieldHolder("name" + i, "value" + i);
//            dataObjectHolder.putDataFieldHolder("name" + i, dataFieldHolder);
//        }
//        System.out.println(getSql("hehehe", dataObjectHolder));
//        List<DataObjectHolder> dataObjectHolders = Lists.newArrayList();
//        dataObjectHolders.add(dataObjectHolder);
//        System.out.println(getParams(dataObjectHolders));

    }

}
