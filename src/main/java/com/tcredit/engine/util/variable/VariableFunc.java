package com.tcredit.engine.util.variable;

import com.google.common.collect.Lists;
import com.tcredit.engine.constants.Constants;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-04-12 10:22
 * @updatedUser: zl.T
 * @updatedDate: 2018-04-12 10:22
 * @updatedRemark:
 * @version:
 */
public class VariableFunc extends Variable {
    private static final String PARENTHESE = "(";
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(VariableFunc.class);

    public VariableFunc(String regexStr, String ruleStr, ProcessContextV2 cxt) {
        super(regexStr, ruleStr);
        this.cxt = cxt;
    }


    /**
     * 上下文
     */
    private ProcessContextV2 cxt;
    /**
     * 值域类型，支持func
     */
    private String type;

    /**
     * 方法名称
     */
    private String functionName;

    /**
     * 方法变量串
     */
    private String functionVar;

    /**
     * 方法参数
     */
    private List<String> params = Lists.newArrayList();


    //${fun|parse(${conf|applyform_unionpay_model_data_url})}

    @Override
    public Object calculate() {
        Object rltVal = null;
        List<String> newParams = Lists.newArrayList();
        Class[] paramType = null;
        if (params != null && !params.isEmpty()) {
            paramType = new Class[params.size()];
            int i = 0;
            for (String param : params) {
                String parse = VariableUtil.parse(param, cxt);
                newParams.add(parse);
                paramType[i++] = String.class;
            }
        }


        try {
            Method m = null;
            if (paramType != null) {
                m = FunctionUtil.class.getDeclaredMethod(functionName, paramType);
            } else {
                m = FunctionUtil.class.getDeclaredMethod(functionName);
            }
            if (m != null) {
                rltVal = m.invoke(null, newParams.toArray());
            }
        } catch (Exception e) {
            LOGGER.error("gid:{},mid:{},变量解析错误，变量:{},异常信息:{}", cxt.get(ProcessContextEnum.GID),
                    cxt.get(ProcessContextEnum.MODULE_ID), getRegexStr(), e);
        }


        if (rltVal == null) {
            LOGGER.warn(String.format("未能解析变量%s", getRegexStr()));
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
            this.functionVar = ruleStr.substring(index + 1, ruleStr.length());
            int i = functionVar.indexOf(PARENTHESE);
            if (i > 0) {
                this.functionName = functionVar.substring(0, i);
                String paramVarStr = functionVar.substring(i + 1, functionVar.length() - 1);
                if (StringUtils.isNotBlank(paramVarStr)) {
                    String[] split = paramVarStr.split(",");
                    this.getParams().addAll(Arrays.asList(split));
                }
            }
        }
    }


    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
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

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionVar() {
        return functionVar;
    }

    public void setFunctionVar(String functionVar) {
        this.functionVar = functionVar;
    }


    public static void main(String[] args) {

    }
}
