package com.github.cosycode.common.ext.proxy;

import com.github.cosycode.common.ext.hub.AbstractClosureProxy;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * <b>Description : </b> 限流闭包代理类
 * <p> 作用是经它代理过的方法(函数式接口实例)在同一时间内只能够允许几个线程运行, 其它的则阻塞等待.
 * <b>created in </b> 2021/4/7
 *
 * @author CPF
 * @since 1.2
 **/
@Slf4j
public class CurrentLimitClosureProxy<T, P, R> extends AbstractClosureProxy<T, P, R> {

    private final Semaphore semaphore;

    public CurrentLimitClosureProxy(int limit, @NonNull T then) {
        super(then);
        check(limit);
        semaphore = new Semaphore(limit);
    }

    public CurrentLimitClosureProxy(int limit, @NonNull T then, @NonNull BiFunction<T, P, R> function) {
        super(then, function);
        check(limit);
        semaphore = new Semaphore(limit);
    }

    public CurrentLimitClosureProxy(int limit, @NonNull T then, @NonNull BiConsumer<T, P> biConsumer) {
        super(then, biConsumer);
        check(limit);
        semaphore = new Semaphore(limit);
    }

    private void check(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit cannot less than 1");
        }
    }

    @Override
    public R closureFunction(P params) {
        try {
            semaphore.acquire();
            biFunction.apply(functional, params);
        } catch (InterruptedException e) {
            log.error("failed to get signal, params: " + params, e);
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();
        }
        return null;
    }
}
