package com.tcredit.engine.util.variable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-08 15:19
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-08 15:19
 * @updatedRemark:
 * @version:
 */
public abstract class Variable {
    /**
     *
     * @param regexStr
     * @param ruleStr
     */
    public Variable(String regexStr, String ruleStr) {
        this.regexStr = regexStr;
        this.ruleStr = ruleStr;
    }

    /**
     * 匹配到的变量表达式，规则串如：${req|idCust}表示从request
     */
    private String regexStr;
    /**
     *规则串，去掉了\\$\\{\\}之后的字符串
     */
    protected String ruleStr;

    /**
     * 计算表达式的值
     * @return
     */
    public abstract Object calculate();

    /**
     * 分解ruleString
     */
    protected abstract void analysisRuleString();

    /**
     * 正则匹配串，格式：\\$\\{ruleStr\\}
     * @return
     */
    public String getRegexStr() {
        return regexStr;
    }

    public void setRegexStr(String regexStr) {
        this.regexStr = regexStr;
    }

    public String getRuleStr() {
        return ruleStr;
    }

    public void setRuleStr(String ruleStr) {
        this.ruleStr = ruleStr;
    }
}
