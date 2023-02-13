package com.github.cosycode.common.util.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/12/9
 * </p>
 *
 * @author pengfchen
 * @since 1.8
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtils {

    public static String filerPrefix = "com.ebay";

    /**
     * Convert Exception Stack to String.
     *
     * @return 当前报错路径堆栈
     */
    @SuppressWarnings("java:S112")
    public static String getPathMessage() {
        try {
            throw new RuntimeException();
        } catch (RuntimeException exception) {
            return getStackMessage(exception);
        }
    }

    /**
     * 获取关键异常堆栈信息.
     * @param e 异常
     * @return String that convert from Exception Stack.
     */
    public static String getKeyStackMessage(Throwable e) {
        if (e == null) {
            return getStackMessage(new RuntimeException("NULL_MESSAGE"));
        }
        String message = e.getMessage();
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getSimpleName());
        if (StringUtils.isNotBlank(message)) {
            sb.append("{").append(message).append("}");
        } else {
            sb.append("[");
            simplifyStackTrace(sb, e.getStackTrace(), 100);
            sb.append("]");
        }
        return sb.toString();
    }

    /**
     * Convert Exception Stack to String.
     *
     * @param e exception
     * @return String that convert from Exception Stack.
     */
    public static String getStackMessage(Exception e) {
        if (e == null) {
            return getStackMessage(new RuntimeException("NULL_MESSAGE"));
        }
        String message = e.getMessage();
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getSimpleName()).append("{").append(message).append("}[");
        simplifyStackTrace(sb, e.getStackTrace(), 5);
        sb.append("]");
        return sb.toString();
    }

    /**
     * convert StackTraceElement from Exception, and put result into a StringBuilder
     */
    private static void simplifyStackTrace(StringBuilder sb, StackTraceElement[] stackTrace, int limitCount) {
        boolean flag = false;
        for (int row = 0; limitCount > 0 && row < stackTrace.length; row++) {
            StackTraceElement element = stackTrace[row];
            String className = element.getClassName();
            // filter class by prefix String
            if (filerPrefix == null || className.startsWith(filerPrefix)) {
                limitCount--;
                if (flag) {
                    sb.append(", ");
                } else {
                    flag = true;
                }
                sb.append(row).append("=");
                sb.append(className.substring(className.lastIndexOf('.') + 1));
                sb.append(":").append(element.getMethodName()).append('(').append(element.getLineNumber()).append(')');
            }
        }
    }

}
