package com.tcredit.engine.exception;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-27 上午10:55
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-27 上午10:55
 * @updatedRemark:
 * @version:
 */
public class XMLParserException extends RuntimeException {



    public XMLParserException() {
    }

    public XMLParserException(String message) {
        super(message);
    }

    public XMLParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLParserException(Throwable cause) {
        super(cause);
    }

    public XMLParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
