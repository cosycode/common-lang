package com.github.cosycode.common.thread;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * <b>Description : </b> 可控制的循环线程(装饰模式), 对 Thread 采用了组合方式, 而不是继承方式
 * <p>
 * <b>设计如下: </b>
 * <br/> <b>线程终止: </b> 只要内置线程调用 interrupt() 方法即视为线程需要终止.
 * <br/> <b>线程等待和唤醒机制: </b> 因为线程调用 interrupt() 方法视为线程需要终止, 因此此处使用 wait + notify 来管理线程等待和唤醒.
 * </p>
 * <b>created in </b> 2020/8/13
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
@Accessors(chain = true)
public class CtrlLoopThreadComp implements AutoCloseable {

    /**
     * 抛出异常调用方法: 打印现场后继续下一次循环
     */
    public static final BiConsumer<CtrlComp, RuntimeException> CATCH_FUNCTION_CONTINUE = CtrlComp::logException;
    /**
     * 参照 Thread 中为每个线程生成一个 Id 的方法, 简单移植过来的.
     * 如果该类, 没有赋值 name, 则根据当前 Number 生成一个 name
     */
    private static int threadInitNumber;
    /**
     * 用于线程启停的锁
     */
    public final Object lock = new Object();
    /**
     * 内置线程对象
     */
    protected final Thread thread;
    /**
     * 线程每次执行的函数, 如果函数返回false, 则线程循环结束
     */
    private final BooleanSupplier booleanSupplier;
    /**
     * 线程是否暂停标记
     */
    @Getter
    private volatile boolean suspend;
    /**
     * 多长时间运行一次(while true 中的一个执行sleep多久)
     */
    @Setter
    private int millisecond;
    /**
     * 处理函数对象, 此处使用 volatile 仅仅保证该引用的可见性
     */
    @SuppressWarnings("java:S3077")
    private volatile CtrlComp ctrlComp;
    /**
     * 等待时间, 这里使用 wait 和 notify 对线程进行唤醒
     */
    private long waitTime;
    /**
     * 返回 false 时调用方法
     */
    private Consumer<CtrlComp> falseConsumer;
    /**
     * 出错时的消费函数接口
     */
    private BiConsumer<CtrlComp, RuntimeException> catchConsumer;
    /**
     * @param booleanSupplier     运行函数, 如果运行中返回 false, 则暂停运行.
     * @param name                线程名称, 若为 empty, 则会自动取一个名字(以 CtrlLoopThreadComp- 开头)
     * @param continueIfException 发生异常时是否继续.
     * @param millisecond         两次循环之间间隔多久(毫秒)
     */
    protected CtrlLoopThreadComp(BooleanSupplier booleanSupplier, String name, boolean continueIfException, int millisecond) {
        this(booleanSupplier, null, continueIfException ? CATCH_FUNCTION_CONTINUE : null, name, millisecond);
    }

    /**
     * CtrlLoopThreadComp 主要构造方法
     * <p>
     * <br/> CtrlLoopThreadComp 里面内置了一个线程, 线程会 每隔 millisecond 毫秒 循环调用 booleanSupplier 方法, 若 millisecond <= 0, 则表示不进行暂停.
     * <br/> 当调用 booleanSupplier 结果返回 true, 则隔 millisecond 毫秒后继续调用 booleanSupplier 方法
     * <br/> 当调用 booleanSupplier 结果返回 false, 则调用 falseConsumer 方法, 若 falseConsumer 为 null, 则不对返回值做任何处理, 继续下一次循环
     * <br/> 当调用 booleanSupplier 时抛出运行时异常, 则调用 catchConsumer 方法, 若 catchConsumer 为 null, 则不对异常做任何处理, 等于将异常抛给虚拟机.
     * </p>
     *
     * @param booleanSupplier 运行函数(不可为 null)
     * @param falseConsumer   booleanSupplier 运行后返回 false 时调用函数
     * @param catchConsumer   booleanSupplier 运行后抛出 异常时 调用该函数
     * @param name            线程名称, 若为 empty, 则会自动取一个名字(以 CtrlLoopThreadComp- 开头)
     * @param millisecond     两次循环之间间隔多久(毫秒), 如果为 0 则表示不暂停.
     */
    protected CtrlLoopThreadComp(BooleanSupplier booleanSupplier, Consumer<CtrlComp> falseConsumer, BiConsumer<CtrlComp, RuntimeException> catchConsumer, String name, int millisecond) {
        this.booleanSupplier = booleanSupplier;
        this.falseConsumer = falseConsumer;
        this.catchConsumer = catchConsumer;
        this.millisecond = millisecond;
        if (StringUtils.isBlank(name)) {
            name = "CtrlLoopThreadComp-" + nextThreadNum();
        }
        thread = new Thread(new CtrlLoopRunnable(), name);
    }

    /**
     * 参照 Thread 中为每个线程生成一个 Id 的方法, 简单移植过来的.
     *
     * @return 一个 Id 编号
     */
    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    /**
     * @param runnable            运行函数
     * @param continueIfException 发生异常时是否继续.
     * @param millisecond         两次循环之间间隔多久(毫秒)
     * @return 创建的线程
     */
    public static CtrlLoopThreadComp ofRunnable(Runnable runnable, boolean continueIfException, int millisecond) {
        return new CtrlLoopThreadComp(() -> {
            runnable.run();
            return true;
        }, null, continueIfException, millisecond);
    }

    /**
     * @param runnable 运行函数
     * @return 创建的线程
     */
    public static CtrlLoopThreadComp ofRunnable(Runnable runnable) {
        return new CtrlLoopThreadComp(() -> {
            runnable.run();
            return true;
        }, null, false, 0);
    }

    /**
     * @param booleanSupplier     运行函数, 如果运行中返回 false, 则暂停运行.
     * @param continueIfException 发生异常时是否继续.
     * @param millisecond         两次循环之间间隔多久(毫秒)
     * @return 创建的线程
     */
    public static CtrlLoopThreadComp ofSupplier(BooleanSupplier booleanSupplier, boolean continueIfException, int millisecond) {
        return new CtrlLoopThreadComp(booleanSupplier, null, continueIfException, millisecond);
    }

    /**
     * @param booleanSupplier 运行函数, 如果运行中返回 false, 则暂停运行.
     * @return 创建的线程
     */
    public static CtrlLoopThreadComp ofSupplier(BooleanSupplier booleanSupplier) {
        return new CtrlLoopThreadComp(booleanSupplier, null, false, 0);
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
     * 线程暂停指定毫秒, 同Object.wait()一样, 若等待时间为0，则表示永久暂停。
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
    @Override
    public void close() {
        if (thread.isInterrupted()) {
            log.warn("线程已经关闭, 请勿重复调用");
        } else {
            thread.interrupt();
        }
    }

    /**
     * 当 loop 循环返回 false 时调用该线程.
     *
     * @param falseConsumer 返回 false 消费线程
     * @return 当前对象本身
     */
    public CtrlLoopThreadComp falseFun(Consumer<CtrlComp> falseConsumer) {
        this.falseConsumer = falseConsumer;
        return this;
    }

    /**
     * 设置出错线程, 如果发生异常, 则会调用该方法
     *
     * @param catchConsumer 出错消费线程
     * @return 当前对象本身
     */
    public CtrlLoopThreadComp catchFun(BiConsumer<CtrlComp, RuntimeException> catchConsumer) {
        this.catchConsumer = catchConsumer;
        return this;
    }

    /**
     * 获取线程名称
     *
     * @return 当前对象内线程名称
     */
    public String getName() {
        return thread.getName();
    }

    /**
     * 设置线程名称
     *
     * @param name 线程名称
     * @return 当前对象本身
     */
    public CtrlLoopThreadComp setName(String name) {
        thread.setName(name);
        return this;
    }

    /**
     * @return 返回唯一的控制器
     */
    public CtrlComp getCtrlComp() {
        if (ctrlComp == null) {
            synchronized (this) {
                if (ctrlComp == null) {
                    ctrlComp = new CtrlComp();
                }
            }
        }
        return ctrlComp;
    }

    @Deprecated
    public CtrlLoopThreadComp setContinueIfException(boolean continueIfException) {
        this.catchConsumer = CATCH_FUNCTION_CONTINUE;
        return this;
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
        @SuppressWarnings({"java:S1119", "java:S112"})
        public void run() {
            final String name = thread.getName();
            log.debug("CtrlLoopThread [{}] start!!!", name);
            outer:
            while (!thread.isInterrupted()) {
                /* 这个地方使用额外的对象锁停止线程, 而不是使用线程本身的停滞机制, 保证一次循环执行完毕后执行停止操作, 而不是一次循环正在执行 booleanSupplier 的时候停止*/
                // 此处之所以使用 while 而不是 if 是因为想要在线程等待过程中或者结束后, 依然可以通过控制 suspend 使得线程可以再次陷入等待, 以及可以最佳等待时间.
                while (suspend) {
                    suspend = false;
                    synchronized (lock) {
                        try {
                            log.debug("CtrlLoopThread [{}] pause!!!", name);
                            if (waitTime > 0) {
                                // 添加临时变量, 使能够在调用 wait 方法之前重置 waitTime 变量, 防止多次调用 暂停方法失效
                                final long waitTmp = waitTime;
                                waitTime = 0;
                                lock.wait(waitTmp);
                            } else {
                                lock.wait();
                            }
                            log.debug("CtrlLoopThread [{}] wake!!!", name);
                        } catch (InterruptedException e) {
                            log.debug("CtrlLoopThread [{}] was interrupted during waiting!!!", name);
                            thread.interrupt();
                            /* 线程中断即意味着线程结束, 此时跳出最外层循环 */
                            break outer;
                        }
                    }
                }
                /* 这个地方是正式执行线程的代码 */
                try {
                    final boolean cont = loop();
                    // 当结果返回 false 同时 falseConsumer 不为 null 时, 调用 falseConsumer 方法, 否则不做任何处理, 继续下一次循环
                    if (!cont && falseConsumer != null) {
                        falseConsumer.accept(getCtrlComp());
                    }
                } catch (RuntimeException e) {
                    /* 如果发生异常则调用 catchConsumer, 若 catchConsumer 为 null, 则封装现场并抛出异常 */
                    if (catchConsumer != null) {
                        catchConsumer.accept(getCtrlComp(), e);
                    } else {
                        throw new RuntimeException(String.format("CtrlLoopThread [%s] processing exception, the thread stop!", name), e);
                    }
                }
                // 控制loop多久循环一次, 防止 CPU 过高占用
                if (millisecond > 0) {
                    try {
                        Thread.sleep(millisecond);
                    } catch (InterruptedException e) {
                        log.debug("CtrlLoopThread [{}] was interrupted during sleep", name);
                        thread.interrupt();
                    }
                }
            }
            log.debug("CtrlLoopThread [{}] end!!!", name);
        }
    }

    /**
     * 当前类的控制器
     */
    public class CtrlComp {

        /**
         * 线程暂停指定毫秒, 同Object.wait()一样, 若等待时间为0，则表示永久暂停。
         * 当线程正在暂停中时, 再次调用该方法, 线程在自动结束等待情况下, 将继续 wait 指定的时间
         *
         * @param waitTime 暂停的时间(毫秒)
         */
        public void pause(long waitTime) {
            CtrlLoopThreadComp.this.pause(waitTime);
        }

        /**
         * 线程暂停
         */
        public void pause() {
            CtrlLoopThreadComp.this.pause();
        }

        /**
         * 继续下一次循环
         */
        public void continueNextLoop() {
        }

        /**
         * 终止内置线程
         */
        public void endCtrlLoopThread() {
            close();
        }

        /**
         * 打印异常
         *
         * @param e 发生异常
         */
        public void logException(RuntimeException e) {
            final String msg = String.format("CtrlLoopThread [%s] processing exception, continue to the next round", getName());
            log.error(msg, e);
        }

    }

}
