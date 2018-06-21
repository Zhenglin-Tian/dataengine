package com.tcredit.engine.util.db;

import org.apache.commons.lang3.RandomUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by chen on 2016/10/21.
 */
public class DBUtil {

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
        return toFormatString(new Date(), "yyyyMMddHHmmssSSS") + RandomUtils.nextInt(100000, 999999);
    }


    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
    }
    public static String toFormatString(Date date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat( pattern);
        return sdf.format( date);
    }

    /**
     * @param data
     * @param pattern 返回时间格式
     * @param type 1-年，2-月，3-周，5-天
     * @param num 要加的数字
     * @return 返回
     */
    public static String afterDayString( Date data, String pattern , int type,int num){
        SimpleDateFormat sdf = new SimpleDateFormat( pattern);
        GregorianCalendar gc=new GregorianCalendar();
        gc.add( type, num);
        return sdf.format( gc.getTime());
    }
}
