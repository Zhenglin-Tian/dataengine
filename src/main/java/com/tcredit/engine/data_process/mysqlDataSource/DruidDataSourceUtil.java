package com.tcredit.engine.data_process.mysqlDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.collect.Maps;
import com.tcredit.engine.constants.Constants;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-12 11:08
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-12 11:08
 * @updatedRemark:
 * @version:
 */
public class DruidDataSourceUtil {
    /**
     * 系统启动时加载
     * 加载classpath://druid_data_source 下的所有${name}.properties
     * 以properties文件中的source_type值为 key,使用配置创建DruidDataSource作为 value
     */
    private static Map<String, DruidDataSource> dataSourcesCache = Maps.newHashMap();

    public static DruidDataSource getDataSource(String type) {
        return dataSourcesCache.get(type);
    }

    /**
     * 创建数据源并保存到缓存中
     *
     * @param properties
     * @return
     */
    public static DruidDataSource createDruidDataSourceAndPutInCache(Properties properties) throws Exception {
        DruidDataSource druidDataSource = createDruidDataSource(properties);
        dataSourcesCache.put(properties.getProperty(Constants.DATA_SOURCE_TYPE_KEY_STRING), druidDataSource);
        return druidDataSource;
    }

    /**
     * 创建数据源
     *
     * @param properties
     * @return
     */
    private static DruidDataSource createDruidDataSource(Properties properties) throws Exception {
        return (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
    }

    public static void main(String[] args) throws FileNotFoundException {

    }


}
