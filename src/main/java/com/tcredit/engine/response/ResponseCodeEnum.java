package com.tcredit.engine.response;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-29 15:38
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-29 15:38
 * @updatedRemark:
 * @version:
 */
public class ResponseCodeEnum {
    /**
     * 枚举值
     */
    private int code;

    /**
     * 枚举描述
     */
    private String message;

    /**
     * 常量都是针对code的设计，code只能内部使用，外部不能传入
     */
    //成功
    public static final int SUCCESS = 1;
    public static final int OUT_SUCCESS = 0;

    //参数有误
    public static final int PARAM_ERROR = -1;

    //没有匹配到流程
    public static final int NO_MATCHED_FLOWCONTAINER = -2;


    //内部处理失败
    public static final int INNER_FAIL = -3;

    //查询结果失败
    public static final int RESULT_FAIL = -4;

    //无数据
    public static final int NO_DATA = -5;


    private ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public static ResponseCodeEnum success() {
        return new ResponseCodeEnum(SUCCESS, "SUCCESS");
    }

    public static ResponseCodeEnum success(String message) {
        return new ResponseCodeEnum(SUCCESS, message);
    }

    public static ResponseCodeEnum paramFail(String msg) {

        return new ResponseCodeEnum(PARAM_ERROR, msg);
    }

    public static ResponseCodeEnum resultFail(String msg) {

        return new ResponseCodeEnum(RESULT_FAIL, msg);
    }
    public static ResponseCodeEnum noData(String msg) {

        return new ResponseCodeEnum(NO_DATA, msg);
    }

    public static ResponseCodeEnum noMathedDataContainer() {
        return new ResponseCodeEnum(NO_MATCHED_FLOWCONTAINER, "没有匹配到可用的数据模块");
    }

    public static ResponseCodeEnum innerFail(String msg) {
        return new ResponseCodeEnum(INNER_FAIL, msg);
    }



}