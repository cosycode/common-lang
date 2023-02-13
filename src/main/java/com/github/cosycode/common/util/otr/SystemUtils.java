package com.github.cosycode.common.util.otr;

import com.github.cosycode.common.lang.BaseRuntimeException;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/5/9
 * </p>
 *
 * @author pengfchen
 **/
public class SystemUtils {

    public static void setForkJoinPoolDefaultCount(int number) {
        if (number < 1) {
            throw new BaseRuntimeException("java.util.concurrent.ForkJoinPool.common.parallelism value need to great than 0 ==> {}", number);
        }
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(number));
    }

}
