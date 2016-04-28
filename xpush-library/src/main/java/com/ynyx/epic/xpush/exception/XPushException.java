package com.ynyx.epic.xpush.exception;

/**
 * Created by Dong on 2016/4/20.
 */
public class XPushException extends RuntimeException {
    public XPushException() {
        super();
    }

    public XPushException(String detailMessage) {
        super(detailMessage);
    }

    public XPushException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public XPushException(Throwable throwable) {
        super(throwable);
    }
}
