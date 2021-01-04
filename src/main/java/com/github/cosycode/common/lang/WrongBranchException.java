package com.github.cosycode.common.lang;

/**
 * <b>Description : </b> 错误分支异常, 用于分支控制时执行到不该执行的分支时抛出
 * <p>
 * <b>created in </b> 2020/3/24
 *
 * @author CPF
 * @since 1.0
 */
public class WrongBranchException extends RuntimeException {

    public WrongBranchException() {
        super();
    }

    public WrongBranchException(String message) {
        super(message);
    }

    public WrongBranchException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongBranchException(Throwable cause) {
        super(cause);
    }
}
