package com.tcredit.engine.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-08 15:56
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-08 15:56
 * @updatedRemark:
 * @version:
 */
public class Constants {
    public static final String FAIL = "-1001";
    public static final String SUCCESS = "0000";
    public static final String SUCCESS_MESSAGE = "成功";
    // 变量表达式元素分隔符
    public static final char VARIABLE_RULE_STRING_SPLIT_CHAR = '|';
    // 变量类型:原样返回的变量
    public static final String VARIABLE_TYPE_ORIGIN = "origin";
    // 变量类型:值域变量 request,response,config,context
    public static final String VARIABLE_TYPE_CONTEXT = "context";
    //
    public static final String VARIABLE_TYPE_FUNC = "func";

    public static final String DATA_SOURCE_TYPE_KEY_STRING = "name";

    public static final String DB_NAME_SIGN = "online";

    public static final List<String> DBS = new ArrayList<String>() {{
        add("test");
        add("std");
        add("tidy");
        add("varcalc");
        add("model");
    }};
}
