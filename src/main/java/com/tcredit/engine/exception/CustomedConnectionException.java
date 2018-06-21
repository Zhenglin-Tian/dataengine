package com.tcredit.engine.exception;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-04 09:19
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-04 09:19
 * @updatedRemark:
 * @version:
 */
public class CustomedConnectionException extends  RuntimeException {


    public CustomedConnectionException() {
    }

    public CustomedConnectionException(String message) {
        super(message);
    }

    public CustomedConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomedConnectionException(Throwable cause) {
        super(cause);
    }

    public CustomedConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
