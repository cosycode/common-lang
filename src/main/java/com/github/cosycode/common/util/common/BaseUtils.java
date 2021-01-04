package com.github.cosycode.common.util.common;

import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <b>Description : </b> 基础工具类
 * <p>
 * <b>created in </b> 2020/4/13
 *
 * @author CPF
 * @since 1.0
 */
public class BaseUtils {

    private BaseUtils() {
    }

    /**
     * 从 [0, range] 中获取cnt个不相同的随机数
     *
     * @param range 范围
     * @param cnt   随机数值
     * @return 从 0-range范围内的不相同的cnt个随机数值
     */
    @SuppressWarnings("all")
    public static List<Integer> getRandomsFromRange(int range, int cnt) {
        if (range < cnt) {
            throw new RuntimeException("range 不应该小于 cnt");
        }

        int pointer = 0;
        int[][] mapping = new int[2][cnt];
        Arrays.fill(mapping[0], -1);
        Arrays.fill(mapping[1], -1);

        List<Integer> list = new ArrayList<>(cnt);

        for (int x = cnt; x > 0; x--) {
            int i = RandomUtils.nextInt(0, range);
            range--;
            if (i < range) {
                int index = ArrUtils.indexOf(mapping[0], i);
                if (index == -1) {
                    list.add(i);
                    index = ArrUtils.indexOf(mapping[0], range);
                    if (index == -1) {
                        mapping[0][pointer] = i;
                        mapping[1][pointer] = range;
                        pointer++;
                    } else {
                        mapping[0][index] = i;
                    }
                } else {
                    index = ArrUtils.indexOf(mapping[0], i);
                    assert index > -1;
                    list.add(mapping[1][index]);
                    int idx2 = ArrUtils.indexOf(mapping[0], range);
                    if (idx2 == -1) {
                        mapping[1][index] = range;
                    } else {
                        mapping[1][index] = mapping[1][idx2];
                        // --
                        mapping[0][idx2] = -1;
                        mapping[1][idx2] = -1;
                    }
                }
            } else if (i == range) {
                int index = ArrUtils.indexOf(mapping[0], range);
                if (index == -1) {
                    list.add(i);
                } else {
                    list.add(mapping[1][index]);
                }
            } else {
                throw new RuntimeException();
            }
        }
        return list;
    }


}
