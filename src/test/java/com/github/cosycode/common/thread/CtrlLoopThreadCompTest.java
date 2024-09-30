package com.github.cosycode.common.thread;

import com.github.cosycode.common.ext.hub.Throws;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/2/20
 * </p>
 *
 * @author CPF
 * @since 1.6
 **/
@Slf4j
@Ignore
public class CtrlLoopThreadCompTest {

    /**
     * 基础功能测试
     */
    @Test
    public void baseTest() {
        final CtrlLoopThreadComp threadComp = CtrlLoopThreadComp.ofRunnable(() -> {
                    final String s = UUID.randomUUID().toString();
                    log.info("-----------------------开始执行线程方法 : {}", s);
                    // 睡眠 200 毫秒, 睡眠期间有异常则直接转换为 RuntimeException 抛出
                    Throws.con(200, Thread::sleep).runtimeExp();
                    log.info("-----------------------结束执行线程方法 : {}", s);
                })
                // 执行出现异常则继续下一次执行
                .catchFun(CtrlLoopThreadComp.CATCH_FUNCTION_CONTINUE)
                // 每 100 毫秒执行一次
                .setMillisecond(300)
                // 线程名称名为 COM-RUN
                .setName("可循环控制线程 1");
        // 启动线程
        log.info(" ======> 启动循环线程");
        threadComp.start();
        Throws.con(2000, Thread::sleep).runtimeExp();

        log.info(" ======> 暂停循环线程");
        threadComp.pause();
        Throws.con(2000, Thread::sleep).runtimeExp();

        log.info(" ======> 启动或唤醒循环线程, 1秒后再次暂停");
        threadComp.startOrWake();
        threadComp.pause(1000);
        Throws.con(2000, Thread::sleep).runtimeExp();

        log.info(" ======> 启动循环线程, 再执行 5 次循环后自动暂停");
        threadComp.pauseAfterLoopTime(5);
        Throws.con(5000, Thread::sleep).runtimeExp();

        log.info(" ======> 启动或唤醒循环线程, 2秒后 线程关闭");
        threadComp.startOrWake();
        Throws.con(2000, Thread::sleep).runtimeExp();
        threadComp.close();
    }

    /**
     * 并发调用几个方法, 看下执行逻辑是否出错
     */
    @Test
    public void ConcurrentTest() throws InterruptedException {
        final CtrlLoopThreadComp threadComp = CtrlLoopThreadComp.ofRunnable(() -> {
                    final String s = UUID.randomUUID().toString();
                    log.info("-----------------------开始执行线程方法 : {}", s);
                    // 睡眠 200 毫秒, 睡眠期间有异常则直接转换为 RuntimeException 抛出
                    Throws.con(RandomUtils.nextInt(0, 3), Thread::sleep).runtimeExp();
                    log.info("-----------------------结束执行线程方法 : {}", s);
                })
                // 执行出现异常则继续下一次执行
                .catchFun(CtrlLoopThreadComp.CATCH_FUNCTION_CONTINUE)
                // 每 100 毫秒执行一次
                .setMillisecond(100)
                // 线程名称名为 COM-RUN
                .setName("可循环控制线程 2");
        // 启动线程
        log.info(" ======> 启动线程");
        threadComp.start();

        final int loop = 100000;
        CountDownLatch countDownLatch = new CountDownLatch(loop);
        IntStream.range(0, loop).parallel().forEach(num -> {
            final int i = RandomUtils.nextInt(0, 5);
            switch (i) {
                case 0:
                    threadComp.pause();
                    break;
                case 1:
                    threadComp.startOrWake();
                    break;
                case 2:
                    threadComp.pause(1);
                    break;
                case 3:
                    threadComp.pause();
                    Throws.con(1, Thread::sleep).runtimeExp();
                    threadComp.wake();
                    break;
                case 4:
                    threadComp.startIfNotStart();
                    break;
                default:
            }
            countDownLatch.countDown();
        });

        countDownLatch.await();
        log.info(" ======> 执行完毕关闭线程");
        threadComp.close();
        threadComp.close();
        assert countDownLatch.getCount() == 0;
    }


    /**
     * 对 Runnable 方法进行循环调用, 两次调用间隔 1000 毫秒
     */
    @Test
    public void baseWhileTest() {
        Runnable runnable =  () -> {
            while (!Thread.currentThread().isInterrupted()) {
                // 执行相关逻辑
                log.info("执行相关逻辑");
                try {
                    // 防止 CPU 被打满
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("线程从睡眠中唤醒");
                }
            }
            log.info("线程结束");
        };

        final Thread thread = new Thread(runnable, "while 循环线程");
        thread.start();

        Throws.con(5000, Thread::sleep).logThrowable();
        thread.interrupt();
    }


    /**
     * 每隔一秒执行打印一次日志.
     */
    @Test
    public void baseWhileCompTest() {
        final CtrlLoopThreadComp threadComp = CtrlLoopThreadComp.ofRunnable(() -> {
                    log.info("执行相关逻辑");
                })
                // 执行出现异常则继续下一次执行
                .catchFun(CtrlLoopThreadComp.CATCH_FUNCTION_CONTINUE)
                // 每 500 毫秒执行一次
                .setMillisecond(500)
                // 线程名称名为 COM-RUN
                .setName("可循环控制线程");

        // 启动线程
        log.info(" ======> 启动线程");
        threadComp.start();

        // 一段时间后(5000毫秒后)暂停
        Throws.con(5000, Thread::sleep).logThrowable();
        threadComp.close();
    }


    private final BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>(1000);

    /**
     * 异步消息处理测试
     */
    @Test
    public void asynchronousQueueTest() {
        // 异步处理器
        final CtrlLoopThreadComp threadComp = CtrlLoopThreadComp.ofRunnable(() -> {
                    try {
                        String str = blockingQueue.take();
                        log.info("异步线程打印信息 : {}", str);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                // 执行出现异常则继续下一次执行
                .catchFun(CtrlLoopThreadComp.CATCH_FUNCTION_CONTINUE)
                .setMillisecond(0)
                .setName("异步处理线程");
        threadComp.start();

        // 即时往队列里面添加消息, 异步处理线程即时处理
        blockingQueue.add("消息1");
        blockingQueue.add("消息2");
        blockingQueue.add("消息3");
        blockingQueue.add("消息4");
        blockingQueue.add("消息5");

        // 主线程睡眠 10 毫秒, 方便队列消息能够处理完成
        Throws.con(10, Thread::sleep).logThrowable();
    }


    /**
     * 异步消息处理测试
     */
    @Test
    public void asynchronousQueueTest2() {
        // 异步处理器
        final CtrlLoopThreadComp threadComp = CtrlLoopThreadComp.ofRunnable(() -> {
                    try {
                        String str = blockingQueue.take();
                        log.info("异步线程打印信息 : {}", str);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                // 执行出现异常则继续下一次执行
                .catchFun(CtrlLoopThreadComp.CATCH_FUNCTION_CONTINUE)
                .setMillisecond(0)
                .setName("异步处理线程");

        // 处理 6 条消息后暂停
        threadComp.pauseAfterLoopTime(6);
        threadComp.start();

        // 即时往队列里面添加消息, 异步处理线程即时处理
        blockingQueue.add("消息1");
        blockingQueue.add("消息2");
        blockingQueue.add("消息3");
        blockingQueue.add("消息4");
        blockingQueue.add("消息5");
        blockingQueue.add("消息6");
        blockingQueue.add("消息7");
        blockingQueue.add("消息8");
        blockingQueue.add("消息9");
        blockingQueue.add("消息10");

        Throws.con(2000, Thread::sleep).logThrowable();
        log.info("发现继续添加消息，不再进行处理");

        log.info("启动线程后继续处理消息");
        threadComp.wake();

        Throws.con(10, Thread::sleep).logThrowable();


    }


    /**
     * 异步消息处理测试
     */
    @Test
    public void asynchronousQueueTest3() {
        // 异步处理器
        final CtrlLoopThreadComp threadComp = CtrlLoopThreadComp.ofSupplier(() -> {
                    String str = "";
                    try {
                         str = blockingQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("成功接收到了字符串 ==> {}", str);
                    // 字符串转数字，转换不成功会抛出异常
                    final int i = Integer.parseInt(str);
                    // 偶数返回 true，奇数返回false
                    return i % 2 == 0;
                })
                .setMillisecond(0)
                .setName("异步处理线程")
                // 执行出现异常则继续下一次执行
                .catchFun((ctrlComp, e) -> {
                    log.error("执行异常的时候发生了错误 : {}", e.getMessage());
                    // 执行错误后，暂停 300 毫秒
                    ctrlComp.pause(300);
                    // 执行错误后，结束循环线程
                    // ctrlComp.endCtrlLoopThread();
                })
                .falseFun(ctrlComp -> {
                    log.warn("执行 loop 循环的之后返回了 false");
                    // 继续执行
                    ctrlComp.continueNextLoop();
                });

        threadComp.start();

        // 即时往队列里面添加消息, 异步处理线程即时处理
        blockingQueue.add("23");
        blockingQueue.add("11");
        blockingQueue.add("32");
        blockingQueue.add("0");
        blockingQueue.add("哈哈");

        Throws.con(10, Thread::sleep).logThrowable();
    }


}
