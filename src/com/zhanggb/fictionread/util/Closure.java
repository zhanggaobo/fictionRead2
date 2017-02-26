package com.zhanggb.fictionread.util;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-22
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public interface Closure<T> {

    public static class VetoException extends FoundationException {
        private static final long serialVersionUID = 7887659760614542342L;
    }

    public static class Execution {
        public static void veto() {
            throw new VetoException();
        }
    }

    void execute(T input);
}
