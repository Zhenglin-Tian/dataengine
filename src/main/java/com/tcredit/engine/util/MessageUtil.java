package com.tcredit.engine.util;

import com.tcredit.engine.constants.ResponseConstants;
import com.tcredit.engine.context.ProcessContext;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.response.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-29 15:34
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-29 15:34
 * @updatedRemark:
 * @version:
 */
public class MessageUtil {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(MessageUtil.class);

    /**
     *
     * @param response
     * @return
     */
    public static OutResponse convertBase2Out(BaseResponse<String> response){
        if (null == response){
            return null;
        }
        OutResponse res = new OutResponse();
        if ("1".equalsIgnoreCase(response.getCode())) {
            res.setCode(ResponseConstants.SUCCESS);
        } else {
            res.setCode(String.valueOf(response.getCode()));
        }
        res.setMessage(response.getMessage());

        return res;
    }

    /**
     * 将BaseRespone转化成外界统一的code形式
     *
     * @param gid      请求标识，通过该gid可以获取有效上下文
     * @param response
     * @return
     */
    public static ResponseData convertBaseToOut(String gid, String mid, BaseResponse<List<TableData>> response) {

        if (null == response) {
            return null;
        }
        ResponseData responseData = new ResponseData();
        try {
            ProcessContext cxt = null;
            if (StringUtils.isNotBlank(gid) && StringUtils.isNotBlank(mid)) {
                String sessionKey =KeyUtil.generateRedisKey(gid,mid);
                String s = RedissonUtil.get(sessionKey);
                cxt = JsonUtil.json2Object(s, ProcessContext.class);
            }
            if (cxt == null) {
                LOGGER.error(String.format("gid:%s,构建返回值时，未能正常构建上下文", gid));
                responseData.setCode(String.valueOf(ResponseCodeEnum.INNER_FAIL));
                responseData.setMsg(response.getMessage());
                responseData.setData(null);
                return responseData;
            }
            if (cxt != null) {
                responseData.setGid(String.valueOf(gid));
                if (mid != null) {
                    responseData.setMid(String.valueOf(mid));
                }
                Object cmid = cxt.get(ProcessContextEnum.CHILD_MODULE_ID);
                if (cmid != null) {
                    responseData.setCmid(String.valueOf(cmid));
                }
            }
            if ("1".equalsIgnoreCase(response.getCode())) {
                responseData.setCode(ResponseConstants.SUCCESS);
            } else {
                responseData.setCode(String.valueOf(response.getCode()));
            }
            responseData.setMsg(response.getMessage());
            responseData.setData(response.getData());

            responseData.setSync(null);
            return responseData;
        } catch (Exception e) {
            LOGGER.error(String.format("gid:%s,构建返回值时，未能正常获取jedis客户端", gid));
            responseData.setCode(String.valueOf(ResponseCodeEnum.INNER_FAIL));
            responseData.setMsg("内部错误");
            responseData.setData(null);
            return responseData;

        }
    }


    public static ResponseData convertBaseToOut(BaseResponse<List<TableData>> response) {

        if (null == response) {
            return null;
        }
        ResponseData responseData = new ResponseData();
        if ("1".equalsIgnoreCase(response.getCode())) {
            responseData.setCode(ResponseConstants.SUCCESS);
        } else {
            responseData.setCode(String.valueOf(response.getCode()));
        }
        responseData.setMsg(response.getMessage());
        responseData.setData(response.getData());

        responseData.setSync(null);
        return responseData;
    }
}
