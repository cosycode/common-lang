package com.github.cosycode.common.util.otr;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/11/26 17:41
 **/
@Slf4j
public class TestUtils {

    private TestUtils(){}

    /**
     * 正常执行 Runnable 函数, 打印 执行 Runnable 函数的执行时长信息
     *
     * @param tag 标记
     * @param runnable 执行函数
     */
    public static void callTime(String tag, Runnable runnable) {
        callTime(tag, () -> {
            runnable.run();
            return true;
        });
    }

    /**
     * 正常执行 supplier 函数, 打印 执行 supplier 函数的执行时长信息
     *
     * @param tag 标记
     * @param supplier 执行函数
     * @param <R> 执行函数返回值类型
     * @return 执行函数的返回值
     */
    public static <R> R callTime(String tag, Supplier<R> supplier) {
        long start = System.nanoTime();
        log.error("[{} : {}] ==> start", start, tag);
        R r = supplier.get();
        long inVal = System.nanoTime() - start;
        log.error("[{} : {}] ==> end, consume time: {} ", start, tag, inVal);
        return r;
    }

}
