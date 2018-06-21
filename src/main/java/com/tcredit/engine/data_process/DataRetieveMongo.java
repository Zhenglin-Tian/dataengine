package com.tcredit.engine.data_process;

import com.tcredit.engine.data_process.mongoDataProcessUtil.MongoDataProcessUtil;
import com.tcredit.engine.dbEntity.RetrieveEntity;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-05 19:55
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-05 19:55
 * @updatedRemark:
 * @version:
 */
public class DataRetieveMongo implements DataRetrieve {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DataRetieveMongo.class);


    @Override
    public List<Map<String, Object>> retrieve(RetrieveEntity entity) {
        if (entity != null) {
            try {
                return MongoDataProcessUtil.queryDataNormal(entity);
            } catch (Exception e) {
                LOGGER.error(String.format("Mongo查询异常，表:%s,异常信息:%s", (entity.step + "_" + entity.db + "_" + entity.tblName).toLowerCase(), e));
            }
        }

        return null;
    }
}
