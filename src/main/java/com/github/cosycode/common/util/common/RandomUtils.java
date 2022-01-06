package com.github.cosycode.common.util.common;

import java.util.Arrays;
import java.util.Random;

/**
 * <b>Description : </b> Random 工具类
 * <p>
 * <b>created in </b> 2021/7/31
 * </p>
 *
 * @author CPF
 * @since 0.1
 **/
public class RandomUtils {

    private RandomUtils(){}

    public static final Random RANDOM = new Random();

    /**
     * 在 [0, range) 的范围中获取n个随机数
     * <p>
     *     例如: 在 [0,10) 的范围内取 3 个数, 必定有一个数在 [0, 8) 内, 先取这个数 n1 (在[0,8)范围内随机),
     *     取完这个数, 则必定有一个数在[0,9)内, 且这个数不是 n1, 因此也在[0,8)范围内随机取个数 n2, 如果n1 = n2 , 则n2 加一...
     *
     *     照上面思想为前提, 所有的随机都是在 [0,8) 范围内取随机数, 之后将取的所有数排序,
     *     再之后第一个数 + 0, 第二个数 +1, 第三个数 + 2 ..., 最终获得所有随机数.
     *
     *     然而, 当 range 很大为 100, 而 n 为 90, 那么从100里面取90个随机数, 不如, 取10个随机数, 之后没有取到的数作为返回值.
     * </p>
     * @param range 范围
     * @param n     n 个随机数
     * @return n个随机数数组
     */
    public static int[] getNumbersInRange(int range, int n) {
        if (range < n) {
            throw new IllegalArgumentException("range 需要大于 n ==> range: " + range + ", n: " + n);
        }
        if (range > 3 && n * 3 >> 1 > range) {
            int t = range - n;
            final int[] arr1 = getOrderNumbersInRange(range, t);
            final int[] arr0 = new int[n];
            for (int i = 0, j = 0, idx = 0; idx < range; idx++) {
                if (j >= t || arr1[j] != idx) {
                    arr0[i++] = idx;
                } else {
                    j++;
                }
            }
            return arr0;
        } else {
            return getOrderNumbersInRange(range, n);
        }
    }

    /**
     * 在 [0, range) 的范围中获取n个不重复的随机数, 返回的随机数组有排序
     * <p>
     *     例如: 在 [0,10) 的范围内取 3 个数, 必定有一个数在 [0, 8) 内, 先取这个数 n1 (在[0,8)范围内随机),
     *     取完这个数, 则必定有一个数在[0,9)内, 且这个数不是 n1, 因此也在[0,8)范围内随机取个数 n2, 如果n1 = n2 , 则n2 加一...
     *
     *     照上面思想为前提, 所有的随机都是在 [0,8) 范围内取随机数, 之后将取的所有数排序,
     *     再之后第一个数 + 0, 第二个数 +1, 第三个数 + 2 ..., 最终获得所有随机数.
     *
     * </p>
     * @param range 范围
     * @param n     n 个随机数
     * @return n个随机数数组
     */
    private static int[] getOrderNumbersInRange(int range, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n 需要大于等于1 ==> n: " + n);
        }
        if (range < n) {
            throw new IllegalArgumentException("range 需要大于 n ==> range: " + range + ", n: " + n);
        }
        if (n == 0) {
            return new int[]{};
        }
        if (n == 1) {
            return new int[]{RANDOM.nextInt(range)};
        }
        final int[] arr = new int[n];
        final int r = range - n + 1;
        for (int i = 0; i < n; i++) {
            arr[i] = RANDOM.nextInt(r);
        }
        Arrays.sort(arr);
        for (int i = 0; i < n; i++) {
            arr[i] += i;
        }
        return arr;
    }

}
