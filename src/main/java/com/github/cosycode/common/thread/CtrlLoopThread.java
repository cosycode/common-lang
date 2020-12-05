package com.github.cosycode.common.thread;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.BooleanSupplier;

/**
 * <b>Description : </b> 可控制的循环线程
 *
 * @author CPF
 * @since 1.0
 * @date 2020/8/13 23:10
 */
@Slf4j
public final class CtrlLoopThread extends Thread {

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

    @Builder
    public CtrlLoopThread(BooleanSupplier booleanSupplier, boolean continueIfException, int millisecond) {
        this.booleanSupplier = booleanSupplier;
        this.continueIfException = continueIfException;
        this.millisecond = millisecond;
    }

    @Override
    public void run() {
        final String name = getName();
        log.debug("CtrlLoopThread [{}] start!!!", name);
        while (!isInterrupted()) {
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
                        interrupt();
                    }
                }
            }
            try {
                final boolean cont = booleanSupplier.getAsBoolean();
                if (!cont) {
                    break;
                }
            } catch (RuntimeException e) {
                // 如果发生异常是否继续执行下一回合
                if (continueIfException) {
                    log.error("CtrlLoopThread [" + name + "] 循环处理异常, 继续执行下一轮", e);
                } else {
                    throw new RuntimeException("CtrlLoopThread [" + name + "] 循环处理异常, 线程停止执行!", e);
                }
            }
            // 控制loop多久循环一次, 防止 CPU 过高占用
            if (millisecond > 0) {
                try {
                    Thread.sleep(millisecond);
                } catch (InterruptedException e) {
                    log.error("CtrlLoopThread [\" + name + \"] 线程sleep中发生错误", e);
                    interrupt();
                }
            }
        }
        log.debug("CtrlLoopThread [{}] end!!!", name);
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

    /**
     * 线程启动或恢复
     */
    public void startOrWake() {
        final State state = getState();
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
                log.warn("wrong invocation! CtrlLoopThread [{}] has ended!!!", getName());
                break;
            default:
        }
    }

    /**
     * 通过 interrupt 停止循环线程, 线程将会在执行完当前循环之后, 自动停止
     */
    public void close() {
        if (isInterrupted()) {
            log.warn("线程已经关闭, 请勿重复调用");
        } else {
            interrupt();
        }
    }

}
