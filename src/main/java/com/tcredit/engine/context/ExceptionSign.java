package com.tcredit.engine.context;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-02-05 09:20
 * @updatedUser: zl.T
 * @updatedDate: 2018-02-05 09:20
 * @updatedRemark:
 * @version:
 */
public class ExceptionSign {
    private boolean exceptionSign =false;
    private String exceptionMsg = "";

    public boolean isExceptionSign() {
        return exceptionSign;
    }

    public void setExceptionSign(boolean exceptionSign) {
        this.exceptionSign = exceptionSign;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }
}
