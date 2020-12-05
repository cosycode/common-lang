package com.github.cosycode.common.base;

/**
 * <b>Description : </b> 带有抛出异常的 Runnable 接口
 *
 * @author CPF
 * @since 1.0
 * @date 2020/6/29
 */
@FunctionalInterface
public interface RunnableWithThrow<E extends Throwable> {

    void run() throws E;

}
