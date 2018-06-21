package com.tcredit.engine.util;

import org.apache.commons.lang3.RandomUtils;

import java.util.Date;
import java.util.UUID;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 09:25
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 09:25
 * @updatedRemark:
 * @version:
 */
public class IDUtil {
    /**
     * 为数据库产生primary_key
     * @return
     */
    public static String createPrimaryKey() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 创建唯一的请求id
     * @return
     */
    public static String createNoBus() {
        return DateUtil.formatDate2StrFromDate(new Date(), "yyyyMMddHHmmssSSS") + RandomUtils.nextInt(100000, 999999);
    }

    /**
     * test
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
        System.out.println(createPrimaryKey());
        System.out.println(createNoBus());
    }
}
