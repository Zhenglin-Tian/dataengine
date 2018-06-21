package com.tcredit.engine.data_process;

import com.tcredit.engine.dbEntity.RetrieveEntity;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-20 17:36
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-20 17:36
 * @updatedRemark:
 * @version:
 */
public interface DataRetrieve {
    /**
     * 基本查询
     * @param entity
     * @return
     */
    List<Map<String,Object>> retrieve(RetrieveEntity entity);

}
