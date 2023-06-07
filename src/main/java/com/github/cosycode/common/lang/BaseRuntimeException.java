package com.github.cosycode.common.lang;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/12/28
 * </p>
 *
 * @author pengfchen
 * @since 1.0
 **/
public class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException(String message, Object... objects) {
        super(String.format(message, objects));
    }

    public BaseRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
