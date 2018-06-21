package com.tcredit.engine.util.variable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.constants.Constants;
import com.tcredit.engine.context.ProcessContext;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.util.JsonUtil;
import org.apache.avro.data.Json;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-01 16:34
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-01 16:34
 * @updatedRemark:
 * @version:
 */
public class VariableUtil {
    /**
     * 将给出的变量串解析成值，如变量串为${request.idCard}则含义为从request作用域中解析idCard的值，request为map
     * processContext 中包含核心作用域：
     * req:由请求参数转换
     * conf：由配置文件转换
     * res：由结果实体转换
     * cxt: 运行上下文
     *
     * @param content
     * @param cxt
     * @return
     */
    public static String parse(String content, ProcessContextV2 cxt) {
        if (StringUtils.isBlank(content)) return null;

        if (content.indexOf("${func|") >= 0) {
            /**
             * 解析函数式变量
             */
            String pattern = "\\$\\{.+\\}+";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(content);
            List<String> variables = Lists.newArrayList();
            while (m.find()) {
                variables.add(m.group());
            }

            //解析变量形式${conf|${req|${conf|${conf|yy}}}}
            String val = null;
            for (String var : variables) {
                Variable variable = getVaribleCalculator(var, cxt);
                Object object = variable.calculate();
                if (object != null) {

                    if (!String.class.isInstance(object)) {
                        val = JsonUtil.toJson(object);
                    } else {
                        val = object.toString();
                    }

                }
            }

            if (val.contains("${")) {
                return null;
            }
            return val;
        } else {
            /**
             * 解析非函数的变量
             */
            // 匹配出所有的规则变量
//        String pattern = "\\$\\{.+?\\}+";
            String pattern = "\\$\\{.+?\\}+";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(content);
            List<String> variables = Lists.newArrayList();
            while (m.find()) {
                variables.add(m.group());
            }

            //解析变量形式${conf|${req|${conf|${conf|yy}}}}
            for (String var : variables) {
                String varx = var;

                String ruleStr = var.substring(2, var.length() - 1).trim();
                String regexStr = null;
                if (containsVar(ruleStr, pattern)) {
                    String parse = parse(ruleStr, cxt);
                    varx = "${" + parse + "}";
                    regexStr = getVar(var, pattern);
                }

                Variable variable = getVaribleCalculator(varx, cxt);

                Object object = variable.calculate();
                if (object != null && !content.equals(object.toString())) {
                    String val = null;
                    if (!String.class.isInstance(object)) {
                        val = JsonUtil.toJson(object);
                    } else {
                        val = object.toString();
                    }
//                if (null != val) {
//                    rltVal = content.replaceAll(escapeExprSpecialChar(variable.getRegexStr()), val);
//                }
                    if (StringUtils.isNotBlank(regexStr)) {
                        content = content.replaceAll(escapeExprSpecialChar(regexStr), val);
                    } else {
                        String s = JsonUtil.toJson(val);
                        s = s.substring(1, s.length() - 1);
                        s = escapeExprSpecialCharForContent(s);
                        content = content.replaceAll(escapeExprSpecialChar(variable.getRegexStr()), s);
                        //content = val;
                    }

                }
            }


            /**
             * 如果返回的content中仍然包含"${"说明变量解析错误，则返回null
             */
            if (content.startsWith("${")) {

                return null;
            }
            return content;
        }
    }


    /**
     *
     */
    private static String getVar(String var, String pattern) {
//        String pattern = "\\$\\{.+?\\}+";
//        String pattern = "\\$\\{.+?\\}+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(var);
        List<String> variables = Lists.newArrayList();
        while (m.find()) {
            variables.add(m.group());
        }
        if (!variables.isEmpty()) {
            return variables.get(0);
        }

        return null;
    }


    /**
     * 判断变量里是否含有别的变量
     *
     * @param var
     * @return
     */
    private static boolean containsVar(String var, String pattern) {
        // 匹配出所有的规则变量
//        String pattern = "\\$\\{.+?\\}+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(var);
        List<String> variables = Lists.newArrayList();
        while (m.find()) {
            variables.add(m.group());
        }
        if (!variables.isEmpty()) {
            return true;
        }

        return false;
    }


    /**
     * @param regexStr
     * @param cxt
     * @return
     */
    public static Variable getVaribleCalculator(String regexStr, ProcessContextV2 cxt) {
        String originRegexStr = regexStr;
        //去掉空白字符串
        regexStr = regexStr.replaceAll("\\s", "");
        //去掉${}
        String ruleStr = regexStr.substring(2, regexStr.length() - 1);
        //如果为空，则认为是直接值类型
        if (StringUtils.isBlank(ruleStr)) {
            return new VariableOrigin(regexStr.trim(), ruleStr.trim());
        }

        String type = getVariableType(ruleStr);
        Variable variable = null;
        if (type.equalsIgnoreCase(Constants.VARIABLE_TYPE_CONTEXT)) {
            variable = new VariableContext(regexStr.trim(), ruleStr.trim(), cxt);
        } else if (type.equalsIgnoreCase(Constants.VARIABLE_TYPE_ORIGIN)) {
            variable = new VariableOrigin(regexStr.trim(), ruleStr.trim());
        } else if (type.equalsIgnoreCase(Constants.VARIABLE_TYPE_FUNC)) {
            variable = new VariableFunc(regexStr.trim(), ruleStr.trim(), cxt);
        } else {
            throw new RuntimeException(String.format("Unknown type variable expression %s, word before first '|' letter" +
                    "must in [context, req, custom, func]"));
        }

        variable.analysisRuleString();
        return variable;

    }


    /**
     * 如果没有'|'，则是直接值类型
     * 如果有'|'，则第一个'|'前的字符串为type，看这个type出现在哪个holder里，就是什么类型。
     * 如果没出现在任何holder里，就是unkown类型
     *
     * @param ruleStr
     * @return
     */
    private static String getVariableType(String ruleStr) {
        int index = ruleStr.indexOf(Constants.VARIABLE_RULE_STRING_SPLIT_CHAR);
        if (index == 0 || index == ruleStr.length() - 1) {
            // 如果'|'字符在第一个或最后一个，格式不正确
            throw new RuntimeException(String.format("Wrong variable format: %s", ruleStr));
        } else if (index > 0) {
            // 取得上下文类型
            String key = ruleStr.substring(0, index);
            if (key.equalsIgnoreCase(ProcessContextEnum.REQUEST.val) ||
                    key.equalsIgnoreCase(ProcessContextEnum.RESPONSE.val) ||
                    key.equalsIgnoreCase(ProcessContextEnum.CONFIG.val) ||
                    key.equalsIgnoreCase(ProcessContextEnum.CONTEXT.val) ||
                    key.equalsIgnoreCase(ProcessContextEnum.UNIT_INFO.val)) {
                return Constants.VARIABLE_TYPE_CONTEXT;
            } else if (key.equalsIgnoreCase(ProcessContextEnum.FUNC.val)) {
                return Constants.VARIABLE_TYPE_FUNC;
            } else {
                throw new RuntimeException(String.format("Wrong variable format: %s", ruleStr));
            }
        } else if (index < 0) {
            // 如果没有'|'字符，就是直接值类型
            return Constants.VARIABLE_TYPE_ORIGIN;
        } else {
            throw new RuntimeException(String.format("Wrong variable format: %s", ruleStr));
        }

    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialChar(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialCharForContent(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
//            String regex = "\\$\\{.*?\\}|\\w+\\$+\\w+|\\$\\{|^\\$+?";
//            Matcher matcher = Pattern.compile(regex).matcher(keyword);
//            while (matcher.find()) {
//                String group = matcher.group();
//                String s = escapeExprSpecialChar(group);
//                keyword = keyword.replace(group, s);
//            }
            keyword = keyword.replaceAll("\\$+","");
        }
        return keyword;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        String var = "${func|parse(${conf|applyform_unionpay_model_data_url})}";
//        String var = "${conf|${req|cooperId}}";
//        ProcessContextV2 ctx = new ProcessContextV2();
//        Map<String, String> request = Maps.newHashMap();
//        request.put("cooperId", "2222");
//        ctx.put(ProcessContextEnum.REQUEST, request);
//
//        Map<String, String> conf = Maps.newHashMap();
//        conf.put("applyform_unionpay_model_data_url", "http://databooster.tcredit.test/tccpMODELapplyUnionpay001");
//        conf.put("xx", "cooperId");
//        conf.put("yy", "xx");
//        conf.put("2222", "xxxxxxxxxxxxxxxx");
//
//        ctx.put(ProcessContextEnum.CONFIG, conf);
//
//        String parse = parse(var, ctx);
//        System.out.println(parse);


        String sss = "$$xx${xx}";
        String s = escapeExprSpecialCharForContent(sss);
        System.out.println(s);
    }

}
