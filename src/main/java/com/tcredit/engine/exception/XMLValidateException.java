package com.tcredit.engine.exception;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-27 15:23
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-27 15:23
 * @updatedRemark:
 * @version:
 */
public class XMLValidateException extends RuntimeException {



    public XMLValidateException() {
    }

    public XMLValidateException(String message) {
        super(message);
    }

    public XMLValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLValidateException(Throwable cause) {
        super(cause);
    }

    public XMLValidateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
