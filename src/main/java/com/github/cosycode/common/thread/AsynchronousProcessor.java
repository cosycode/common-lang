package com.github.cosycode.common.thread;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <b>Description : </b> 异步处理器
 * <p>
 * <b>created in </b> 2018/9/19
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class AsynchronousProcessor<T> extends CtrlLoopThreadComp {

    /**
     * 有消息的消息函数处理接口
     */
    @Getter
    protected final Predicate<T> thenFun;

    /**
     * 出错时的消费函数接口
     */
    @Getter
    protected final Consumer<T> catchFun;

    /**
     * 存放待处理的消息
     */
    @Getter
    protected final BlockingQueue<T> blockingQueue;

    /**
     * @param blockingQueue 阻塞缓存队列
     * @param thenFun       消息的消息函数处理接口(不可为空)
     * @param catchFun      出错时的消费函数接口
     * @param millisecond   线程多久处理一次(毫米), 为0, 表示不 sleep
     */
    public AsynchronousProcessor(@NonNull BlockingQueue<T> blockingQueue, @NonNull Predicate<T> thenFun, Consumer<T> catchFun, int millisecond) {
        super(null, true, millisecond);
        this.blockingQueue = blockingQueue;
        this.thenFun = thenFun;
        this.catchFun = catchFun;
        Validate.isTrue(millisecond >= 0, "millisecond:%s 不能小于0", millisecond);
    }

    /**
     * @param thenFun 消息的消息函数处理接口(不可为空)
     * @param <T>     AsynchronousProcessor对象中的模板T
     * @return AsynchronousProcessor 的实例对象
     */
    public static <T> AsynchronousProcessor<T> ofConsumer(@NonNull Consumer<T> thenFun) {
        return new AsynchronousProcessor<>(new LinkedBlockingQueue<>(), t -> {
            thenFun.accept(t);
            return true;
        }, null, 0);
    }

    /**
     * @param thenFun 消息的消息函数处理接口(不可为空)
     * @param <T>     AsynchronousProcessor对象中的模板T
     * @return AsynchronousProcessor 的实例对象
     */
    public static <T> AsynchronousProcessor<T> ofPredicate(@NonNull Predicate<T> thenFun) {
        return new AsynchronousProcessor<>(new LinkedBlockingQueue<>(), thenFun, null, 0);
    }

    /**
     * 添加一个函数, 方便子类继承
     */
    @Override
    protected boolean loop() {
        // 获取处理对象
        T t;
        try {
            t = blockingQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("AsynchronousProcessor.take() 阻塞时, 线程中断", e);
        }
        // 处理对象
        if (thenFun != null) {
            boolean isSuccess = thenFun.test(t);
            // 如果失败则执行错误消费函数接口
            if (!isSuccess && catchFun != null) {
                catchFun.accept(t);
            }
        }
        return true;
    }

    /**
     * 向异步缓存队列中添加处理对象
     *
     * @param t 处理对象
     */
    public void add(T t) {
        if (t == null) {
            return;
        }
        blockingQueue.add(t);
    }

    /**
     * 如果发生异常是否继续
     *
     * @param continueIfException 发生异常是否继续
     * @return 当前实例对象
     */
    @Override
    public AsynchronousProcessor<T> setContinueIfException(boolean continueIfException) {
        super.setContinueIfException(continueIfException);
        return this;
    }

    /**
     * 多长时间运行一次(while true 中的一个执行sleep多久)
     *
     * @param millisecond 每次循环后睡眠时间
     * @return 当前实例对象
     */
    @Override
    public AsynchronousProcessor<T> setMillisecond(int millisecond) {
        super.setMillisecond(millisecond);
        return this;
    }

    /**
     * @param name 线程名称
     * @return 当前实例对象
     */
    @Override
    public AsynchronousProcessor<T> setName(String name) {
        super.setName(name);
        return this;
    }

    /**
     * @return 当前异步队列中的数据大小
     */
    public int getSize() {
        return blockingQueue.size();
    }

}
