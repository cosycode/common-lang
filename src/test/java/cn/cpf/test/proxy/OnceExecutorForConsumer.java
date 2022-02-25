package cn.cpf.test.proxy;

import com.github.cosycode.common.ext.proxy.OnceExecClosureProxy;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/2/21
 * </p>
 *
 * @author CPF
 * @since
 **/
public class OnceExecutorForConsumer<T> {

    private final Lock lock = new ReentrantLock();

    /**
     * 被代理的方法
     */
    private final Consumer<T> then;

    private Consumer<T> skip;

    public OnceExecutorForConsumer(Consumer<T> then) {
        this.then = then;
    }

    /**
     * 代理方法
     *
     * <p>
     *     每次调用新建一个线程对参数进行处理, 主线程不阻塞, 分线程竞争锁, 抢到运行 then, 抢不到运行 skip
     * </p>
     */
    public T proxy(final T e) {
        if (lock.tryLock()) {
            try {
                if (then != null) {
                    then.accept(e);
                }
            } finally {
                lock.unlock();
            }
        } else {
            if (skip != null) {
                skip.accept(e);
            }
        }
        return null;
    }

    public static <T> T of(T then) {
        return new OnceExecClosureProxy<>(then).proxy();
    }

    public OnceExecutorForConsumer<T> skip(Consumer<T> skip) {
        this.skip = skip;
        return this;
    }

}
