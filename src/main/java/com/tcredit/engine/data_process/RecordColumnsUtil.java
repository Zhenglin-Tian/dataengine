package com.tcredit.engine.data_process;

import com.google.common.collect.Lists;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.PropertiesUtil;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-03 18:11
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-03 18:11
 * @updatedRemark:
 * @version:
 */
public class RecordColumnsUtil {

    /**
     * 获取行记录的所有列，后期根据元数据管理系统，获取表的所有字段
     * @param tableData
     * @return
     */
    public static List<String> getRecordColumns(TableData tableData) {
        List<String> keys = Lists.newArrayList();
        if (tableData != null && !tableData.getData().isEmpty()) {
            Set<String> strings = tableData.getData().get(0).keySet();
            if (strings != null && !strings.isEmpty()) {
                for (String s : strings) {
                    //如果该列为数据库自动插入字段则排除该字段
                    if (PropertiesUtil.getTm_columns().contains(s.toLowerCase())) {
                        continue;
                    } else {
                        keys.add(s);
                    }
                }
            }
        }

        List<String> tm_columns = PropertiesUtil.getTm_columns();
        for (String tm:tm_columns){
            if (!keys.contains(tm)){
                keys.add(tm);
            }
        }
        return keys;
    }
}
