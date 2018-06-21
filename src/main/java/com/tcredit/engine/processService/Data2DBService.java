package com.tcredit.engine.processService;

import com.tcredit.engine.conf.Step;
import com.tcredit.engine.response.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-30 18:09
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-30 18:09
 * @updatedRemark:
 * @version:
 */
public interface Data2DBService {
    /**
     * 将处理过的数据入库
     * @param data
     */
    ResponseData data2DB(String gid, String rid, String mid, String step, String dbName, String tableName, List<Map<String,Object>> data, Map<String,Object> otherData, Step s);
    /**
     * 参数获取
     * @param request
     * @return
     */
    ResponseData data2DB(HttpServletRequest request);

}
