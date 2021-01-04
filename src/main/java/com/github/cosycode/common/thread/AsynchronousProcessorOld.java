package com.github.cosycode.common.thread;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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
 * @see AsynchronousProcessor
 * @since 1.0
 * @deprecated 1.1 replaced by {@link AsynchronousProcessor}
 **/
@Slf4j
@Deprecated
public class AsynchronousProcessorOld<T> {

    /**
     * 有消息的消息函数处理接口
     */
    private final Predicate<T> disposeFun;

    /**
     * 出错时的消费函数接口
     */
    private final Consumer<T> errFun;

    /**
     * 存放待处理的消息
     */
    @Getter
    private final BlockingQueue<T> blockingQueue;

    /**
     * 多长时间运行一次(while true 中的一个执行sleep多久)
     * 可以在运行时动态调整
     */
    @Setter
    private int millisecond;

    /**
     * 当前运行的线程
     */
    private Thread thread;

    /**
     * 用于线程启停的锁
     */
    public final Object lock = new Object();

    /**
     * 线程是否暂停标记
     */
    @Getter
    private volatile boolean suspend;

    /**
     * @param blockingQueue 阻塞缓存队列
     * @param disposeFun    消息的消息函数处理接口(不可为空)
     * @param errFun        出错时的消费函数接口
     * @param millisecond   线程多久处理一次(毫米), 为0, 表示不 sleep
     */
    public AsynchronousProcessorOld(@NonNull BlockingQueue<T> blockingQueue, @NonNull Predicate<T> disposeFun, Consumer<T> errFun, int millisecond) {
        this.blockingQueue = blockingQueue;
        this.disposeFun = disposeFun;
        this.errFun = errFun;
        Validate.isTrue(millisecond >= 0, "millisecond:%s 不能小于0", millisecond);
        this.millisecond = millisecond;
    }

    /**
     * @param disposeFun  消息的消息函数处理接口(不可为空)
     * @param millisecond 线程多久处理一次(毫米), 为0, 表示不 sleep
     */
    public AsynchronousProcessorOld(@NonNull Predicate<T> disposeFun, int millisecond) {
        this(new LinkedBlockingQueue<>(), disposeFun, null, millisecond);
    }

    /**
     * 线程调度方法类
     */
    @SuppressWarnings("java:S3776")
    private class ProcessorRunnable implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                while (suspend) {
                    synchronized (lock) {
                        try {
                            log.info("AsynchronousProcessorOld Thread 已经暂停!!!");
                            lock.wait();
                            log.info("AsynchronousProcessorOld Thread 已经恢复!!!");
                        } catch (InterruptedException e) {
                            log.error("线程暂停发生错误", e);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                try {
                    // 获取处理对象
                    T t = blockingQueue.take();
                    boolean isSuccess = false;
                    // 处理对象
                    if (disposeFun != null) {
                        isSuccess = disposeFun.test(t);
                    }
                    // 如果失败则执行错误消费函数接口
                    if (!isSuccess && errFun != null) {
                        errFun.accept(t);
                    }
                    // 防止 CPU 过高占用
                    if (millisecond > 0) {
                        Thread.sleep(millisecond);
                    }
                } catch (InterruptedException e) {
                    log.error("线程发生错误", e);
                    Thread.currentThread().interrupt();
                } catch (RuntimeException e) {
                    // 添加运行时异常, 防止发生运行时异常县城停止
                    log.error("异步处理器发生处理异常", e);
                }
            }
            log.info("AsynchronousProcessorOld Thread 已经结束!!!");
        }
    }

    /**
     * 启动线程
     */
    public void start() {
        if (thread == null || thread.isInterrupted()) {
            synchronized (this) {
                if (thread == null || thread.isInterrupted()) {
                    thread = new Thread(new ProcessorRunnable());
                    thread.start();
                }
            }
            return;
        }
        log.warn("线程已启动, 请勿重复调用");
    }

    public void add(T t) {
        if (t == null) {
            return;
        }
        blockingQueue.add(t);
    }

    /**
     * 线程暂停
     */
    public void pause() {
        if (thread == null || thread.isInterrupted()) {
            return;
        }
        suspend = true;
    }

    /**
     * 线程恢复
     */
    public void resume() {
        if (thread == null || thread.isInterrupted()) {
            log.warn("线程不存在或已经调用线程关闭方法, 请勿重复调用");
            return;
        }
        if (suspend) {
            synchronized (lock) {
                if (suspend) {
                    suspend = false;
                }
                lock.notifyAll();
            }
        }
    }

    /**
     * 线程关闭
     */
    public void closeThread() {
        if (thread == null || thread.isInterrupted()) {
            log.warn("线程不存在或已经调用线程关闭方法, 请勿重复调用");
            return;
        }
        thread.interrupt();
    }

}
