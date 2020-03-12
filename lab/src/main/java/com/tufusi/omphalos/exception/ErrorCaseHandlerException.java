package com.tufusi.omphalos.exception;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description
 */
public class ErrorCaseHandlerException extends RuntimeException {

    public ErrorCaseHandlerException(String msg) {
        super(msg);
    }

    public ErrorCaseHandlerException(Throwable throwable) {
        super(throwable);
    }

    public ErrorCaseHandlerException(String msg, String throwableStack) {
        super(msg, new Throwable(throwableStack));
    }

    public static String traceToString(int startIndex, Object[] stackArray) {
        if (stackArray == null) {
            return "null";
        }

        int iMax = stackArray.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = startIndex; ; i++) {
            b.append(stackArray[i]);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append("\n");
        }
    }
}
