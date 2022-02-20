package com.github.cosycode.common.thread;

import com.github.cosycode.common.ext.hub.Throws;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/2/20
 * </p>
 *
 * @author CPF
 * @since
 **/
@Slf4j
public class CtrlLoopThreadCompTest {

    public void baseTest() {
        final CtrlLoopThreadComp threadComp = CtrlLoopThreadComp.ofRunnable(() -> {
                    log.info("开始执行线程方法");
                    // 睡眠 200 毫秒, 睡眠期间有异常则直接转换为 RuntimeException 抛出
                    Throws.con(200, Thread::sleep).runtimeExp();
                    log.info("结束执行线程方法");
                })
                // 执行出现异常则继续下一次执行
                .catchFun(CtrlLoopThreadComp.CATCH_FUNCTION_CONTINUE)
                // 每 100 毫秒执行一次
                .setMillisecond(100)
                // 线程名称名为 COM-RUN
                .setName("可循环控制线程 1");
        // 启动线程
        log.info(" ======> 启动循环线程");
        threadComp.start();

        log.info(" ======> 启动循环线程, 等待两秒后暂停");
        Throws.con(2000, Thread::sleep).runtimeExp();
        threadComp.pause();
        Throws.con(5000, Thread::sleep).runtimeExp();

        log.info(" ======> 启动循环线程, 1秒后再次暂停");
        threadComp.startOrWake();
        threadComp.pause(1000);
        Throws.con(5000, Thread::sleep).runtimeExp();

        log.info(" ======> 启动循环线程, 再执行 5 次循环后自动暂停");
        threadComp.pauseAfterLoopTime(5);
        log.info(" ======> 主线程 睡眠 5秒");
        Throws.con(5000, Thread::sleep).runtimeExp();

        log.info(" ======> 启动线程, 2秒后 线程关闭");
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

        final int loop = 10000;
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

}
