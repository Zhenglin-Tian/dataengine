package com.tcredit.engine.util.variable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-08 16:27
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-08 16:27
 * @updatedRemark:
 * @version:
 */
public class VariableOrigin extends Variable {

    /**
     *
     * @param regexStr
     * @param ruleStr
     */
    public VariableOrigin(String regexStr, String ruleStr) {
        super(regexStr, ruleStr);
    }

    @Override
    public String calculate() {
        return ruleStr;
    }

    @Override
    protected void analysisRuleString() {
    }
}