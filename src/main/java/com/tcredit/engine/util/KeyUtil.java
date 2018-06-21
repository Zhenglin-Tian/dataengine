package com.tcredit.engine.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-05 16:39
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-05 16:39
 * @updatedRemark:
 * @version:
 */
public class KeyUtil {

    /**
     * 生成session key
     *
     * @param gid
     * @param mid
     * @return
     */
    public static String generateRedisKey(String gid, String mid) {
        if (StringUtils.isBlank(gid) || StringUtils.isBlank(mid)) {
            return null;
        }
        return gid + "_" + mid;
    }

    /**
     * 生成 分布式锁名称
     *
     * @param gid
     * @param mid
     * @param lockName
     * @return
     */
    public static String generateRedisKey(String gid, String mid, String lockName) {
        if (StringUtils.isBlank(gid) || StringUtils.isBlank(mid) || StringUtils.isBlank(lockName)) {
            return null;
        }
        return gid + "_" + mid + "_" + lockName;
    }


    /**
     * 生成 请求计数key
     *
     * @param gid
     * @param mid
     * @param lockName
     * @return
     */
    public static String generateRedisReqKeyName(String gid, String mid, String step, String lockName) {
        if (StringUtils.isBlank(gid) || StringUtils.isBlank(mid) || StringUtils.isBlank(step) || StringUtils.isBlank(lockName)) {
            return null;
        }
        return gid + "_" + mid + "_" + step + "_" + lockName;
    }


    /**
     *
     */

}
