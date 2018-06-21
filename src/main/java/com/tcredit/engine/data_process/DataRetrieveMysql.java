package com.tcredit.engine.data_process;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.constants.Constants;
import com.tcredit.engine.data_process.mysqlDataSource.DruidDataSourceUtil;
import com.tcredit.engine.dbEntity.AntifraudRetrieveEntity;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-20 17:42
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-20 17:42
 * @updatedRemark:
 * @version:
 */
public class DataRetrieveMysql implements DataRetrieve {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataRetrieveMysql.class);
    private static String SELECT = "SELECT ";
    private static String FROM = " FROM ";
    private static String WHERE = " WHERE ";
    private static String AND = " AND ";
    private static String OR = " OR ";

    /**
     * 数据查询服务，关系数据库
     *
     * @param entity
     * @return
     */
    @Override
    public List<Map<String, Object>> retrieve(RetrieveEntity entity) {
        if (entity == null || StringUtils.isBlank(entity.tblName) || StringUtils.isBlank(entity.gid)) {
            return null;
        }
        List<Map<String, Object>> data = Lists.newArrayList();
        Connection connection = null;
        Statement pstm = null;
        try {
            connection = DruidDataSourceUtil.getDataSource(Constants.DB_NAME_SIGN).getConnection();
            pstm = connection.createStatement();
            String sql = getSql(entity);
            LOGGER.info(String.format("数据查询：库:%s,表:%s,生成的查询sql:%s", entity.db, entity.tblName, sql));

            ResultSet rs = pstm.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            List<String> columns = Lists.newArrayList();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(rsmd.getColumnName(i));
            }
            while (rs.next()) {
                Map<String, Object> map = Maps.newHashMap();
                for (String column : columns) {
                    Object object = rs.getObject(column);

                    if (object == null) {
                        map.put(column, "");
                    } else {
                        if (java.util.Date.class.isInstance(object)) {
                            map.put(column, DateUtil.formatDate2StrFromDate((java.util.Date) object, DateUtil.DATE_FORMAT_yMdHms));
                        } else {
                            map.put(column, object);
                        }
                    }
                }
                data.add(map);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("dbEntity:%s,tableName:%，数据查询异常，异常信息：%s", entity.db, entity.tblName, e));
            throw new RuntimeException(String.format("dbEntity:%s,tableName:%s,数据chaxun失败，信息：%s", entity.db, entity.tblName, e.getMessage()));
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    private String getSql(RetrieveEntity entity) {
        /**
         * 生成表名  阶段_库_表名
         */
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(entity.step)){
            sb.append(entity.step).append("_");
        }
        if (StringUtils.isNotBlank(entity.db)){
            sb.append(entity.db).append("_");
        }
        sb.append(entity.tblName);

        String tableName = sb.toString().toLowerCase();

        sb = new StringBuilder();
        sb.append(SELECT);
        if (entity.columns == null || entity.columns.isEmpty()) {
            sb.append("*");
        } else {
            int i = 1;
            int size = entity.columns.size();
            for (String column : entity.columns) {
                if (i != size) {
                    sb.append(" ").append(column).append(",");
                    i++;
                } else {
                    sb.append(" ").append(column);
                }
            }
        }

        sb.append(FROM).append(tableName.toLowerCase()).append(WHERE);
        sb.append("gid='").append(entity.gid).append("';");


        return sb.toString();
    }





    public static void main(String[] args) throws SQLException {
//        String configPath = Object.class.getResource("/").getPath() + "druid_data_source";
//        File[] configFiles = new File(configPath).listFiles();
//        for (File file : configFiles) {
//            Properties config = new Properties();
//            try {
//                InputStream is = new FileInputStream(file.getAbsolutePath());
//                config.load(is);
//                DruidDataSourceUtil.createDruidDataSourceAndPutInCache(config);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
        RetrieveEntity entity = new RetrieveEntity();
        entity.db = "tidy";
        entity.tblName = "tidy_test";
        entity.gid = "4";
//        entity.columns.add("k1");
//        entity.columns.add("k2");
//        entity.columns.add("k3");
//        entity.columns.add("k4");

        AntifraudRetrieveEntity e = new AntifraudRetrieveEntity();
//        e.gid = "1";
        e.step = "std";
        e.db = "std";
        e.tblName = "antifraudvariable";
//        e.bid="all";
        e.nameRevar="xxx,yyy,bb";
        e.nameInvar="aaaa,bbbb,ccc";


    }
}
