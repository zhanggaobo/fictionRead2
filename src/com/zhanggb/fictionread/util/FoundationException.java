package com.zhanggb.fictionread.util;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-22
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public class FoundationException extends RuntimeException {

    private static final long serialVersionUID = -2167705868135900188L;

    public FoundationException() {
    }

    public FoundationException(String s) {
        super(s);
    }

    public FoundationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FoundationException(Throwable throwable) {
        super(throwable);
    }
}

