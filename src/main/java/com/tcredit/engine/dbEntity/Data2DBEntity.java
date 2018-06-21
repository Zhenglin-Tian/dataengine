package com.tcredit.engine.dbEntity;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-28 13:37
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-28 13:37
 * @updatedRemark:
 * @version:
 */
public class Data2DBEntity {
    /**
     * 数据模块id
     */
    public String mid;
    /**
     * 子模块id
     */
    public String cmid;
    /**
     * 全局gid
     */
    public String gid;
    /**
     * 数据报告id
     */
    public String rid;
    /**
     * 数据处理环节
     */
    public String step;
    /**
     * 数据入库名称
     */
    public String dbName;
    /**
     * 表名称
     */
    public String tableName;
    /**
     * 数据
     */
    public List<Map<String,Object>> data;


}
