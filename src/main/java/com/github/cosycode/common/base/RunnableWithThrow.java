package com.github.cosycode.common.base;

/**
 * <p>
 * <b>created in </b> 2020/6/29
 *
 * @author CPF
 * @see java.lang.Runnable
 * @since 1.0
 */
@FunctionalInterface
public interface RunnableWithThrow<E extends Exception> extends SerialFunctional{

    /**
     * @see     java.lang.Thread#run()
     */
    void run() throws E;

}
