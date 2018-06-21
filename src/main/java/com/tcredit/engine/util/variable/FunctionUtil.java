package com.tcredit.engine.util.variable;

import com.tcredit.engine.data_process.DataStorage;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-04-12 10:10
 * @updatedUser: zl.T
 * @updatedDate: 2018-04-12 10:10
 * @updatedRemark:
 * @version:
 */
public class FunctionUtil {
    private static final String URL_SPLIT = "/";
    private static final String MODEL_PATTERN = "([Mm][Oo][Dd][Ee][Ll])";

    public static String parse(String raw) {
        if (StringUtils.isBlank(raw)) return null;
        int i = raw.lastIndexOf(URL_SPLIT);
        if (i < 0) return null;
        String rawStr = raw.substring(i + 1, raw.length());
        Pattern pattern = Pattern.compile(MODEL_PATTERN);
        Matcher m = pattern.matcher(rawStr);

        if (m.find()) {
            int start = m.start();
            String group = m.group();
            String substring = rawStr.substring(start + group.length(), rawStr.length());
            String s1=substring.substring(0,7);
            String s2=substring.substring(7,substring.length());

            return s1+ DataStorage.SPLIT_LINE+s2;
        }

        return null;
    }


    public static void main(String[] args) throws Exception {
        Method parse = FunctionUtil.class.getDeclaredMethod("parse", String.class);
        parse.setAccessible(true);
        Object invoke = parse.invoke(null, "http://172.19.160.163/tccpMODELjz6rk00001");
        if (invoke != null) {
            System.out.println(invoke.toString());
        }
    }
}
