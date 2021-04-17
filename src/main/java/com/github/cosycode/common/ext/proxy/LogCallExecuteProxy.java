package com.github.cosycode.common.ext.proxy;

import com.github.cosycode.common.ext.hub.AbstractClosureProxy;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * <b>Description : </b> 闭包代理类: 作用是 在被代理类调用的前后, 打印出调用的参数和返回的结果, 以及运行的时间等信息
 * <p>
 * <b>created in </b> 2021/4/6
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class LogCallExecuteProxy<T, P, R> extends AbstractClosureProxy<T, P, R> {

    public LogCallExecuteProxy(T functional) {
        super(functional);
    }

    public LogCallExecuteProxy(T functional, BiFunction<T, P, R> function) {
        super(functional, function);
    }

    public LogCallExecuteProxy(T functional, BiConsumer<T, P> biConsumer) {
        super(functional, biConsumer);
    }

    /**
     * 打印 执行 Runnable 函数的执行时长信息, 以及调用参数与返回值
     */
    @Override
    public R closureFunction(P params) {
        // 计算开始调用时间
        final long start = System.nanoTime();
        final long threadId = Thread.currentThread().getId();
        // 参数
        final String paramString;
        if (params != null) {
            paramString = params.getClass().isArray() ? Arrays.toString((Object[]) params) : params.toString();
        } else {
            paramString = "";
        }
        log.info("[{}] ==> call start ==> params : [{}]", threadId, paramString);
        final R result = biFunction.apply(functional, params);
        // 计算调用时间
        final long inVal = System.nanoTime() - start;
        log.info("[{}] ==> end, return: {}, consume time: {} ", threadId, result, inVal);
        return result;
    }

}
