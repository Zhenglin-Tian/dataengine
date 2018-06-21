package com.tcredit.engine.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by zl.T on 2016/10/29.
 */
public class PropertiesUtil {

    private static Map<String, String> map = Maps.newHashMap();
    private static Map<String, String> serviceMap = Maps.newHashMap();
    private static Map<String, String> dataProdMap = Maps.newHashMap();

    /**
     * 表里关于时间的字段，由系统自动插入的字段，如插入时间time_ist,time_upd等
     */
    private static List<String> tm_columns = Lists.newArrayList();

    static {
        init();
    }


    /**
     * 解析config.properties
     */
    public static void init() {
        map = readProperties("config.properties");
        serviceMap = readProperties("serviceConfig.properties");
        dataProdMap = readProperties("data_prod.properties");
        Map<String, String> stringStringMap = readProperties("recordInsertTimeSign.properties");
        String recordInsertTimeColumns = stringStringMap.get("recordInsertTimeColumns").toLowerCase();
        String[] split = recordInsertTimeColumns.split(",");
        tm_columns.addAll(Arrays.asList(split));
    }

    /**
     * 读取properties
     *
     * @param pathName
     * @return
     */
    public static Map<String, String> readProperties(String pathName) {
        Map<String, String> m = Maps.newHashMap();
        Properties prop = new Properties();
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(pathName);
        try {
            prop.load(new InputStreamReader(is, "UTF-8"));

            Iterator<Map.Entry<Object, Object>> iterator = prop.entrySet().iterator();
            for (; iterator.hasNext(); ) {
                Map.Entry<Object, Object> next = iterator.next();
                m.put(next.getKey().toString(), next.getValue().toString());
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("can't find your " + pathName);
        }
        return m;
    }

    public static String getString(String key) {
        return map.get(key);
    }

    public static Map<String, String> getMap() {
        return map;
    }

    public static void setMap(Map<String, String> map) {
        PropertiesUtil.map = map;
    }

    public static Boolean getBoolean(String key) {
        return Boolean.parseBoolean(key);
    }

    public static Set<String> keys() {
        return map.keySet();
    }

    public static Map<String, String> getServiceMap() {
        return serviceMap;
    }

    public static void setServiceMap(Map<String, String> serviceMap) {
        PropertiesUtil.serviceMap = serviceMap;
    }

    public static List<String> getTm_columns() {
        return tm_columns;
    }

    public static void setTm_columns(List<String> tm_columns) {
        PropertiesUtil.tm_columns = tm_columns;
    }

    public static Map<String, String> getDataProdMap() {
        return dataProdMap;
    }

    public static void setDataProdMap(Map<String, String> dataProdMap) {
        PropertiesUtil.dataProdMap = dataProdMap;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }

}
