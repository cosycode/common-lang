package com.github.cosycode.common.thread;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BooleanSupplier;

/**
 * <b>Description : </b> 可控制的循环线程(装饰模式), 与 CtrlLoopThread 的区别是对 Thread 采用了组合方式, 而不是继承方式
 * <p>
 * <b>created in </b> 2020/8/13
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
@Accessors(chain = true)
public class CtrlLoopThreadComp {

    private static int threadInitNumber;

    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    /**
     * 线程每次执行的函数, 如果函数返回false, 则线程循环结束
     */
    private final BooleanSupplier booleanSupplier;

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
     * 如果发生异常是否继续
     */
    @Setter
    private boolean continueIfException;

    /**
     * 多长时间运行一次(while true 中的一个执行sleep多久)
     */
    @Setter
    private int millisecond;

    /**
     * 等待时间
     */
    private long waitTime;

    @Getter
    private final Thread thread;

    public static CtrlLoopThreadComp ofRunnable(Runnable runnable, boolean continueIfException, int millisecond) {
        return new CtrlLoopThreadComp(() -> {
            runnable.run();
            return true;
        }, null, continueIfException, millisecond);
    }

    public static CtrlLoopThreadComp ofRunnable(Runnable runnable) {
        return new CtrlLoopThreadComp(() -> {
            runnable.run();
            return true;
        }, null, false, 0);
    }

    public static CtrlLoopThreadComp ofSupplier(BooleanSupplier booleanSupplier, boolean continueIfException, int millisecond) {
        return new CtrlLoopThreadComp(booleanSupplier, null, continueIfException, millisecond);
    }

    public static CtrlLoopThreadComp ofSupplier(BooleanSupplier booleanSupplier) {
        return new CtrlLoopThreadComp(booleanSupplier, null, false, 0);
    }

    protected CtrlLoopThreadComp(BooleanSupplier booleanSupplier, boolean continueIfException, int millisecond) {
        this(booleanSupplier, null, continueIfException, millisecond);
    }

    protected CtrlLoopThreadComp(BooleanSupplier booleanSupplier, String name, boolean continueIfException, int millisecond) {
        this.booleanSupplier = booleanSupplier;
        this.continueIfException = continueIfException;
        this.millisecond = millisecond;
        if (StringUtils.isBlank(name)) {
            name = "CtrlLoopThreadComp-" + nextThreadNum();
        }
        thread = new Thread(new CtrlLoopRunnable(), name);
    }

    /**
     * loop函数, 添加一个函数, 方便子类继承
     *
     * @return 当前函数是否运行成功
     */
    protected boolean loop() {
        return booleanSupplier != null && booleanSupplier.getAsBoolean();
    }

    /**
     * <b>Description : </b> 可控制的循环线程的runnable内部类, 控制CtrlLoopThreadComp的执行方式
     * <p>
     * <b>created in </b> 2020/8/13
     *
     * @author CPF
     * @since 1.0
     **/
    private class CtrlLoopRunnable implements Runnable {

        @Override
        public void run() {
            final String name = thread.getName();
            log.debug("CtrlLoopThread [{}] start!!!", name);
            while (!thread.isInterrupted()) {
                /* 这个地方使用额外的对象锁停止线程, 而不是使用线程本身的停滞机制, 保证一次循环执行完毕后执行停止操作, 而不是一次循环正在执行 booleanSupplier 的时候停止*/
                while (suspend) {
                    synchronized (lock) {
                        try {
                            log.debug("CtrlLoopThread [{}] pause!!!", name);
                            if (waitTime > 0) {
                                // 添加临时变量, 使能够在调用 wait 方法之前重置 waitTime 变量, 防止多次调用 暂停方法失效
                                final long waitTmp = waitTime;
                                waitTime = 0;
                                suspend = false;
                                lock.wait(waitTmp);
                            } else {
                                lock.wait();
                            }
                            log.debug("CtrlLoopThread [{}] wake!!!", name);
                        } catch (InterruptedException e) {
                            log.debug("CtrlLoopThread [{}] error!!!", name);
                            thread.interrupt();
                        }
                    }
                }
                try {
                    final boolean cont = loop();
                    if (!cont) {
                        break;
                    }
                } catch (RuntimeException e) {
                    // 如果发生异常是否继续执行下一回合
                    if (continueIfException) {
                        log.error("CtrlLoopThread [" + name + "] processing exception, continue to the next round", e);
                    } else {
                        throw new RuntimeException("CtrlLoopThread [" + name + "] processing exception, the thread stop!", e);
                    }
                }
                // 控制loop多久循环一次, 防止 CPU 过高占用
                if (millisecond > 0) {
                    try {
                        Thread.sleep(millisecond);
                    } catch (InterruptedException e) {
                        log.error("CtrlLoopThread [" + name + "] processing exception in sleep", e);
                        thread.interrupt();
                    }
                }
            }
            log.debug("CtrlLoopThread [{}] end!!!", name);
        }
    }

    /**
     * 线程暂停指定毫秒
     * 当线程正在暂停中时, 再次调用该方法, 线程在自动结束等待情况下, 将继续 wait 指定的时间
     *
     * @param waitTime 暂停的时间(毫秒)
     */
    public void pause(long waitTime) {
        this.waitTime = waitTime;
        suspend = true;
    }

    /**
     * 线程暂停
     */
    public void pause() {
        pause(0);
    }

    /**
     * 线程恢复
     */
    public void wake() {
        if (suspend) {
            this.waitTime = 0;
            synchronized (lock) {
                if (suspend) {
                    suspend = false;
                }
                lock.notifyAll();
            }
        }
    }

    public void start() {
        thread.start();
    }

    /**
     * 线程启动或恢复
     */
    public void startOrWake() {
        final Thread.State state = thread.getState();
        switch (state) {
            case NEW:
                start();
                break;
            case BLOCKED:
            case RUNNABLE:
            case WAITING:
            case TIMED_WAITING:
                wake();
                break;
            case TERMINATED:
                log.warn("wrong invocation! CtrlLoopThread [{}] has ended!!!", thread.getName());
                break;
            default:
        }
    }

    /**
     * 通过 interrupt 停止循环线程, 线程将会在执行完当前循环之后, 自动停止
     */
    public void close() {
        if (thread.isInterrupted()) {
            log.warn("线程已经关闭, 请勿重复调用");
        } else {
            thread.interrupt();
        }
    }

    public CtrlLoopThreadComp setName(String name) {
        thread.setName(name);
        return this;
    }

    public String getName() {
        return thread.getName();
    }

}
