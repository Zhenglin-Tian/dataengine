package com.tcredit.engine.util.variable;

import com.tcredit.engine.constants.Constants;
import com.tcredit.engine.context.ProcessContext;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.util.JsonUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-08 15:30
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-08 15:30
 * @updatedRemark:
 * @version:
 */
public class VariableContext extends Variable {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(VariableContext.class);

    /**
     *
     * @param regexStr
     * @param ruleStr
     * @param cxt
     */
    public VariableContext(String regexStr, String ruleStr, ProcessContextV2 cxt) {
        super(regexStr, ruleStr);
        this.cxt = cxt;
    }

    /**
     * 上下文
     */
//    private Map<String, Object> contextHolder;
    private ProcessContextV2 cxt;
    /**
     * 值域类型，支持req(request),conf(config),res(response)
     */
    private String type;

    /**
     * 值域的键值
     */
    private String key;

    @Override
    public Object calculate() {
        Object rltVal = null;
//        Object o = getContextHolder().get(type);
        Object o = null;
        if (type.equalsIgnoreCase(ProcessContextEnum.CONFIG.val) ||
                type.equalsIgnoreCase(ProcessContextEnum.REQUEST.val) ||
                type.equalsIgnoreCase(ProcessContextEnum.RESPONSE.val) ||
                type.equalsIgnoreCase(ProcessContextEnum.UNIT_INFO.val)) {
            o = cxt.getProcessScopeDataHolder().get(type);
            if (o == null) return null;
            if (Map.class.isInstance(o)) {
                Map<String, String> map = (Map<String, String>) o;
                rltVal = map.get(key);

            } else if (ResponseData.class.isInstance(o)) {
                ResponseData obr = (ResponseData) o;
                Field[] fields = obr.getClass().getDeclaredFields();
                try {
                    for (Field field : fields) {
                        if (key.equalsIgnoreCase(field.getName())) {
                            field.setAccessible(true);
                            rltVal = field.get(obr);
                            break;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("变量解析出错，错误信息：", e);
                }

            }
        } else {
            o = cxt.getProcessScopeDataHolder();
            rltVal = cxt.getProcessScopeDataHolder().get(key);
        }

        if (rltVal == null) {
            LOGGER.warn(String.format("未能解析变量%s,作用域：%s", getRegexStr(), JsonUtil.toJson(o)));
        }
        return rltVal;
    }

    @Override
    protected void analysisRuleString() {
        int index = ruleStr.indexOf(Constants.VARIABLE_RULE_STRING_SPLIT_CHAR);
        if (index <= 0 || index == ruleStr.length() - 1) {
            throw new RuntimeException(String.format("Wrong context variable format: %s", getRegexStr()));
        } else if (index > 0) {
            this.type = ruleStr.substring(0, index);
            this.key = ruleStr.substring(index + 1, ruleStr.length());
        }
    }


    public ProcessContextV2 getCxt() {
        return cxt;
    }

    public void setCxt(ProcessContextV2 cxt) {
        this.cxt = cxt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static void main(String[] args) {


        String xx = "{\"mid\":\"dvd161003\",\"cmid\":\"dvd161003001\",\"gid\":\"1\",\"sync\":true,\"step\":\"tidy\",\"data\":[{\"dbName\":\"testdb\",\"tableName\":\"test\",\"data\":[{\"k1\":\"v1\",\"k2\":\"v2\",\"k3\":\"v3\",\"k4\":\"v4\"},{\"k11\":\"v11\",\"k22\":\"v21\",\"k33\":\"v31\",\"k44\":\"v41\"}]}]}";
        System.out.println(xx);
        ResponseData obr = JsonUtil.json2Object(xx, ResponseData.class);
        ProcessContextV2 pcxt = new ProcessContextV2();
        pcxt.put("res", obr);

        pcxt.put("xxx", "zhangkan");
        System.out.println(pcxt);
        VariableContext variableContext = new VariableContext("${cxt|xxx}", "cxt|xxx", pcxt);

        variableContext.analysisRuleString();

        System.out.println(variableContext.calculate());
    }
}
