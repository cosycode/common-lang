package com.github.cosycode.common.lang;

/**
 * <b>Description : </b> 执行 action 发生异常
 * <p>
 * <b>created in </b> 2019/12/13
 *
 * @author CPF
 * @since 1.0
 */
public class ActionExecException extends RuntimeException {

    public ActionExecException() {
        super();
    }

    public ActionExecException(String actionName) {
        super("发生异常: " + actionName);
    }

    public ActionExecException(String actionName, Throwable cause) {
        super("发生异常: " + (actionName == null ? "" : actionName), cause);
    }

    public ActionExecException(Throwable cause) {
        super(cause);
    }
}
