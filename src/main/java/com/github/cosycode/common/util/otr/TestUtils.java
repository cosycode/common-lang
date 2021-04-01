package com.github.cosycode.common.util.otr;

import com.github.cosycode.common.ext.bean.DoubleBean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * <b>Description : </b> 一个测试工具类
 * <p>
 * <b>created in </b> 2020/11/26
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

    /**
     * 格式化数字(%,.3f: 每三位加一个`,`, 保留三位小数)
     *
     * @param number 数字
     * @return 格式化的数字
     */
    private static String numberStr(double number) {
        return String.format("%,.3f", number);
    }

    /**
     * @return 随机Id
     */
    private static String tagId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 循环执行 Runnable 函数, 打印 执行 Runnable 函数循环 loop 次的执行时长信息
     *
     * @param tag      标记
     * @param runnable 执行函数
     * @param loop     循环次数
     * @return 执行此函数耗费时间
     */
    public static long callLoopTime(String tag, Runnable runnable, int loop) {
        long start = System.nanoTime();
        log.info("[{}] ==> loop: {}, start", tag, loop);
        for (int i = 0; i < loop; i++) {
            runnable.run();
        }
        final long end = System.nanoTime();
        // 循环loop次间隔纳秒
        long totalNs = end - start;
        // 单次间隔纳秒
        long averageNs = totalNs == 0 ? totalNs : totalNs / loop;
        // 循环loop次间隔毫秒
        final long number = totalNs / 1000000L;
        log.info("[{}] ==> loop: {}, end, consume: {}ms, averageNs: {}ns", tag, loop, numberStr(number), numberStr(averageNs));
        return totalNs;
    }

    /**
     * 正常执行 Runnable 函数, 打印 执行 Runnable 函数的执行时长信息
     *
     * @param tag      标记
     * @param runnable 执行函数
     * @param loops    循环次数
     */
    public static void callLoopTime(String tag, Runnable runnable, int[] loops) {
        for (int loop : loops) {
            callLoopTime(tag, runnable, loop);
        }
    }

    /**
     * 正常执行 Runnable 函数, 打印 执行 Runnable 函数的执行时长信息
     * 从 10 次循环开始执行, 若执行总时间小于 millisecond, 则 循环次数 以指数倍增, 再次执行
     *
     * @param tag         标记
     * @param runnable    执行函数
     * @param millisecond 限制时间
     */
    public static void callLimitTime(String tag, Runnable runnable, int millisecond) {
        final long limit = (long) millisecond * 1000_000;
        int time = 2;
        long timeConsuming = 0;
        while (timeConsuming < limit) {
            if (timeConsuming > 1_000_000_000) {
                time = time * 2;
            } else {
                time = time * 5;
            }
            timeConsuming = callLoopTime(tag, runnable, time);
        }
    }

    /**
     * 正常执行 Runnable 函数, 打印 执行 Runnable 函数的执行时长信息
     *
     * @param tag      标记
     * @param runnable 执行函数
     * @return 执行时长(ns)
     */
    public static long callTime(String tag, Runnable runnable) {
        long start = System.nanoTime();
        log.info("[{} : {}] ==> start", tag, start);
        runnable.run();
        final long end = System.nanoTime();
        long inVal = end - start;
        log.info("[{} : {}] ==> end, consume time: {} ", tag, end, inVal);
        return inVal;
    }

    /**
     * 正常执行 supplier 函数, 打印 执行 supplier 函数的执行时长信息
     *
     * @param tag      标记
     * @param supplier 执行函数
     * @param <R>      执行函数返回值类型
     * @return 执行函数的返回值
     */
    @SuppressWarnings("unchecked")
    public static <R> DoubleBean<Long, R> callTime(String tag, Supplier<R> supplier) {
        final Object[] r = new Object[1];
        final long time = callTime(tag, () -> {
            r[0] = supplier.get();
        });
        return DoubleBean.of(time, (R) r[0]);
    }

    /**
     * 正常执行 Runnable 函数, 打印 执行 Runnable 函数的执行时长信息
     *
     * @param runnable 执行函数
     * @return 执行时长(ns)
     */
    public static long callTime(Runnable runnable) {
        return callTime(tagId(), runnable);
    }

    /**
     * 正常执行 supplier 函数, 打印 执行 supplier 函数的执行时长信息
     *
     * @param supplier 执行函数
     * @param <R>      执行函数返回值类型
     * @return 执行函数的返回值
     */
    public static <R> DoubleBean<Long, R> callTime(Supplier<R> supplier) {
        return callTime(tagId(), supplier);
    }

}
