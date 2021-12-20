package com.github.cosycode.common.thread;

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
@SuppressWarnings("unused")
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
     * 内置线程对象
     */
    protected final Thread thread;
    /**
     * 线程每次执行的函数, 如果函数返回false, 则线程循环结束
     */
    private final BooleanSupplier booleanSupplier;
    /**
     * 多长时间运行一次(while true 中的一个执行sleep多久)
     */
    @Setter
    private int millisecond;
    /**
     * 运行类
     */
    private final CtrlLoopRunnable ctrlLoopRunnable;
    /**
     * 处理函数对象, 此处使用 volatile 仅仅保证该引用的可见性
     */
    @SuppressWarnings("java:S3077")
    private volatile CtrlComp ctrlComp;
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
        this.ctrlLoopRunnable = new CtrlLoopRunnable();
        this.thread = new Thread(this.ctrlLoopRunnable, StringUtils.isBlank(name) ? "CtrlLoopThreadComp-" + nextThreadNum() : name);
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
     * 线程暂停
     */
    public void pause() {
        ctrlLoopRunnable.changeState(3, 0, 0);
    }

    /**
     * 线程暂停指定毫秒, 同Object.wait()一样, 若等待时间为0，则表示永久暂停。
     * 当线程正在暂停中时, 再次调用该方法, 线程在自动结束等待情况下, 将继续 wait 指定的时间
     *
     * @param waitTime 暂停的时间(毫秒), 若为0，则表示永久暂停。
     */
    public void pause(long waitTime) {
        ctrlLoopRunnable.changeState(3, waitTime, 0);
    }

    /**
     * 多少次运行之后暂停.
     *
     * @param loopTime 次数
     */
    public void pauseAfterLoopTime(int loopTime) {
        ctrlLoopRunnable.changeState(2, 0, loopTime);
    }

    /**
     * 线程恢复
     */
    public void wake() {
        ctrlLoopRunnable.changeState(1, 0, 0);
    }

    /**
     * 内置线程启动
     */
    public void start() {
        thread.start();
    }

    /**
     * 若没有启动的话, 则启动
     */
    public void startIfNotStart() {
        if (Thread.State.NEW == thread.getState()) {
            thread.start();
        }
    }

    /**
     * @return 内置线程状态
     */
    public Thread.State getThreadState() {
        return thread.getState();
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

    /**
     * @param continueIfException 发生异常后是否继续下一次循环
     * @return 对象本身
     * @deprecated 通过设置 catchConsumer 来控制异常后处理的方式
     */
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
        /**
         * 用于线程启停的锁
         */
        private final Object lock = new Object();
        /**
         * 添加了线程状态 state
         * <p>
         * <br/>添加 state 的好处是 使得更改 state 状态时变得容易理解, 最主要的目的就是方法 {@link CtrlLoopRunnable#changeState(int, long, int)}
         * <br/>
         * <br/> <b>0: </b>初始状态, 表示 state 还未被修改
         * <br/> <b>1: </b>持续运行状态
         * <br/> <b>2: </b>临时运行状态, 指定次数后转换为暂停状态, 此时 waitAfterLoopCount 有意义; 若 waitAfterLoopCount > 0, 则指定次数后转换为永久暂停状态, 否则马上转为永久暂停状态
         * <br/> <b>3: </b>临时运行状态, 即将被暂停, 此时 waitTime 有意义; 若 waitTime>0, 则转为临时暂停状态, 否则转为永久暂停状态.
         * <br/> <b>4: </b>临时暂停状态, 指定时间后被唤醒
         * <br/> <b>5: </b>永久暂停状态, 需要使用 notify 唤醒
         * <br/> <b>-1: </b>终止状态, 此时修改 state 已经没有意义
         */
        private volatile int state;
        /**
         * 线程正处在 loop 方法循环里面
         */
        private volatile int codeRunLocation;
        /**
         * 等待时间, 这里使用 wait 和 notify 对线程进行唤醒
         */
        private long waitTime;
        /**
         * 在多少次循环后暂停标记
         * <br/> <b>-1: </b> 持续运行标记
         * <br/> <b>0: </b> 运行至检查点, 触发暂停事件, 该值转为 -1
         * <br/> <b>n(>0): </b> 运行至检查点, 该值减1
         */
        @Setter
        private int waitAfterLoopCount;

        /**
         * <br/> <b>1: </b>持续运行状态
         * <br/> <b>2: </b>临时运行状态, 指定次数后转换为暂停状态, 此时 waitAfterLoopCount 有意义
         * <br/> <b>3: </b>临时运行状态, 即将被暂停, 此时 waitTime 有意义
         *
         * @param state             {@link CtrlLoopRunnable#state}
         * @param waitTime          等待时间, state=3时有意义, 若 waitTime>0, 则转为临时暂停状态, 否则转为永久暂停状态.
         * @param waitAfterLoopTime 循环指定次数后暂停, state=2时有意义, 若 waitAfterLoopCount > 0, 则指定次数后转换为永久暂停状态, 否则马上转为永久暂停状态
         */
        protected void changeState(final int state, final long waitTime, final int waitAfterLoopTime) {
            synchronized (lock) {
                switch (state) {
                    case 1:
                        this.waitTime = 0;
                        this.waitAfterLoopCount = 0;
                        lock.notifyAll();
                        break;
                    case 2:
                        this.waitTime = 0;
                        if (codeRunLocation == 2) {
                            // 此时运行在执行自定义函数之前, 次数判定之后, 因此此时需要将次数 - 1
                            this.waitAfterLoopCount = waitAfterLoopTime - 1;
                        } else {
                            this.waitAfterLoopCount = waitAfterLoopTime;
                        }
                        lock.notifyAll();
                        break;
                    case 3:
                        this.waitTime = waitTime;
                        this.waitAfterLoopCount = 0;
                        lock.notifyAll();
                        break;
                    default:
                }
                this.state = state;
            }
        }

        @Override
        @SuppressWarnings({"java:S1119", "java:S112"})
        public void run() {
            // 线程在启动之前, 可以不是0
            if (state == 0) {
                state = 1;
            }
            final String name = thread.getName();
            log.debug("CtrlLoopThread [{}] start!!!", name);
            outer:
            while (!thread.isInterrupted()) {
                // 临时运行状态, 指定次数后转换为暂停状态, 此时 waitAfterLoopCount 有意义
                if (state == 2) {
                    synchronized (lock) {
                        if (state == 2) {
                            if (waitAfterLoopCount > 0) {
                                waitAfterLoopCount--;
                            } else {
                                state = 3;
                                waitTime = 0;
                            }
                        }
                    }
                }
                codeRunLocation = 2;
                /* 这个地方使用额外的对象锁停止线程, 而不是使用线程本身的停滞机制, 保证一次循环执行完毕后执行停止操作, 而不是一次循环正在执行 booleanSupplier 的时候停止*/
                // 临时运行状态, 即将被暂停, 此时 waitTime 有意义, waitTime=0, 则转为永久暂停状态, waitTime>0, 则转为临时暂停状态
                if (state == 3) {
                    synchronized (lock) {
                        // 此处之所以使用 while 而不是 if 是因为想要在线程等待过程中或者结束后, 依然可以通过控制 state 使得线程可以再次陷入等待, 以及可以最佳等待时间.
                        while (state == 3) {
                            log.debug("CtrlLoopThread [{}] pause!!!", name);
                            try {
                                // 添加添加临时变量防止幻读
                                final long waitTmp = waitTime;
                                if (waitTmp > 0) {
                                    waitTime = 0;
                                    state = 4;
                                    lock.wait(waitTmp);
                                } else {
                                    state = 5;
                                    lock.wait();
                                }
                            } catch (InterruptedException e) {
                                log.debug("CtrlLoopThread [{}] was interrupted during waiting!!!", name);
                                thread.interrupt();
                                /* 线程中断即意味着线程结束, 此时跳出最外层循环 */
                                break outer;
                            }
                            log.debug("CtrlLoopThread [{}] wake!!!", name);
                        }
                    }
                }
                codeRunLocation = 3;
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
            state = -1;
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
