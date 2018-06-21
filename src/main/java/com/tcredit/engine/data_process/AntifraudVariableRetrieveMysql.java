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
 * @since: 2018-01-03 21:18
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-03 21:18
 * @updatedRemark:
 * @version:
 */
@Deprecated
public class AntifraudVariableRetrieveMysql implements DataRetrieve {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(AntifraudVariableRetrieveMysql.class);
    private static String SELECT = "SELECT ";
    private static String FROM = " FROM ";
    private static String WHERE = " WHERE ";
    private static String AND = " AND ";
    private static String OR = " OR ";
    @Override
    public List<Map<String, Object>> retrieve(RetrieveEntity entity) {
        if (AntifraudRetrieveEntity.class.isInstance(entity)) {
            return antifraudVariableRetrieve((AntifraudRetrieveEntity) entity);
        }
        return null;
    }


    public List<Map<String, Object>> antifraudVariableRetrieve(AntifraudRetrieveEntity entity) {
        if (entity == null || StringUtils.isBlank(entity.tblName)) {
            return null;
        }
        List<Map<String, Object>> data = Lists.newArrayList();
        Connection connection = null;
        Statement pstm = null;
        try {
            connection = DruidDataSourceUtil.getDataSource(Constants.DB_NAME_SIGN).getConnection();
            pstm = connection.createStatement();
            String sql = getSql2(entity);
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

    /**
     * 根据给定的entity创建多条件的复杂查询
     *
     * @param entity
     * @return
     */
    private static String getSql2(AntifraudRetrieveEntity entity) {
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



        sb.append(FROM).append(tableName.toLowerCase());

        if (StringUtils.isNotBlank(entity.gid)) {
            String tempSql = sb.toString();
            int i = tempSql.lastIndexOf(WHERE.trim());
            /**
             * 说明sql包含了where
             */
            if (i > 0) {
                sb.append(AND).append("gid='").append(entity.gid).append("'");
            } else {
                /**
                 * 说明sql中还未添加where
                 */
                sb.append(WHERE).append("gid='").append(entity.gid).append("'");
            }
        }

        if (StringUtils.isNotBlank(entity.bid)) {
            String tempSql = sb.toString();
            int i = tempSql.lastIndexOf(WHERE.trim());
            /**
             * 说明sql包含了where
             */
            if (i > 0) {
                sb.append(AND).append("bid='").append(entity.bid).append("'");
            } else {
                /**
                 * 说明sql中还未添加where
                 */
                sb.append(WHERE).append("bid='").append(entity.bid).append("'");
            }
        }

        if (StringUtils.isNotBlank(entity.nameInvar)) {
            String tempSql = sb.toString();
            int i = tempSql.lastIndexOf(WHERE.trim());
            /**
             * 说明sql包含了where
             */
            //nameInvar多个用逗号分隔
            String[] split = entity.nameInvar.split(",");
            if (i > 0) {
                sb.append(AND).append("(");
                for (int k = 0; k < split.length; k++) {
                    sb.append("nameinvar='").append(split[k]).append("'");
                    if (k < split.length - 1) {
                        sb.append(OR);
                    }
                }
                sb.append(")");
            } else {
                /**
                 * 说明sql中还未添加where
                 */
                sb.append(WHERE).append("(");
                for (int k = 0; k < split.length; k++) {
                    sb.append("nameinvar='").append(split[k]).append("'");
                    if (k < split.length - 1) {
                        sb.append(OR);
                    }
                }
                sb.append(")");
            }
        }

        if (StringUtils.isNotBlank(entity.nameRevar)) {
            String tempSql = sb.toString();
            int i = tempSql.lastIndexOf(WHERE.trim());
            /**
             * 说明sql包含了where
             */
            //nameInvar多个用逗号分隔
            String[] split = entity.nameRevar.split(",");
            if (i > 0) {
                sb.append(AND).append("(");
                for (int k = 0; k < split.length; k++) {
                    sb.append("namerevar='").append(split[k]).append("'");
                    if (k < split.length - 1) {
                        sb.append(OR);
                    }
                }
                sb.append(")");
            } else {
                /**
                 * 说明sql中还未添加where
                 */
                sb.append(WHERE).append("(");
                for (int k = 0; k < split.length; k++) {
                    sb.append("namerevar='").append(split[k]).append("'");
                    if (k < split.length - 1) {
                        sb.append(OR);
                    }
                }
                sb.append(")");
            }
        }


        sb.append(";");


        return sb.toString();
    }
}
