package com.github.cosycode.common.base;

/**
 * <b>Description : </b> 带有抛出异常的 Runnable 接口
 * <p>
 * <b>created in </b> 2020/6/29
 *
 * @author CPF
 * @see java.lang.Runnable
 * @since 1.0
 */
@FunctionalInterface
public interface RunnableWithThrow<E extends Exception> extends SerialFunctional{

    void run() throws E;

}
