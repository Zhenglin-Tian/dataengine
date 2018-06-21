package com.tcredit.engine.data_process;

import com.tcredit.engine.response.TableData;

import java.sql.SQLException;
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
public interface DataStorage {
    String HBASE_3d_DS_PERIOD_TB = "rc_dataengine_datasource_period";
    String HBASE_DEFAULT_FAMILY_NAME = "default_column_family";
    String MODEL_STEP_TABLE_NAME = "model_score_score";
    String MODEL_ID_SIGN = "model_id";
    String SPLIT_LINE = "_";
    String SPLIT_LINE_MID = "-";

    void storage(String gid, String rid, String step, TableData tableData, Map<String, Object> otherData) throws Exception;
}
