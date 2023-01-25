package com.github.cosycode.common.util.common;

import com.github.cosycode.common.lang.ShouldNotHappenException;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * <b>Description : </b> 数组工具类
 * <p>
 * <b>created in </b> 2020/3/27
 *
 * @author CPF
 * @since 1.0
 */
public class ArrUtils {

    private ArrUtils() {
    }

    /**
     * 获取一维数组中相邻 number 个数的最大乘积
     *
     * @param arr    待处理的数组
     * @param number 相邻数量
     * @return throw new RuntimeException (arr.length &lt; number)
     */
    public static int getMaxProductInArr(final int[] arr, final int number) {
        int len = arr.length;
        if (len < number) {
            throw new IllegalArgumentException("arr太小: arr.length: " + arr.length + ", number: " + number);
        }
        int[] num = Arrays.copyOf(arr, number);

        int product = Arrays.stream(num).reduce((a, b) -> a * b).orElseThrow(ShouldNotHappenException::new);
        int cur = 0;
        int tmpProdect;
        for (int i = number; i < len; i++) {
            num[cur] = arr[i];
            tmpProdect = Arrays.stream(num).reduce((a, b) -> a * b).orElseThrow(ShouldNotHappenException::new);
            if (tmpProdect > product) {
                product = tmpProdect;
            }
            cur++;
            cur = cur % number;
        }
        return product;
    }

    /**
     * 转置矩阵, 矩阵必须是方形矩阵
     *
     * @param matrix 矩阵数组
     */
    public static void transposeMatrix(final Object[][] matrix) {
        if (matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("matrix.length != matrix[0].length ==> (" + matrix.length + ", " + matrix[0].length + ")");
        }
        int length = matrix.length;
        Object tmp;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < i; j++) {
                tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
    }

    /**
     * 转置矩阵
     *
     * @param matrix 矩阵数组
     */
    public static void transposeMatrix(final int[][] matrix) {
        if (matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("matrix.length != matrix[0].length ==> (" + matrix.length + ", " + matrix[0].length + ")");
        }
        int length = matrix.length;
        int tmp;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < i; j++) {
                tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
    }


    /**
     * 将一个字符串数组转换为int数组
     *
     * @param strArr 待处理的字符串数组
     * @return 转换后的int数组
     */
    public static int[] transStrArrToIntArr(final String[] strArr) {
        int len = strArr.length;
        int[] intArr = new int[len];
        for (int i = 0; i < len; i++) {
            intArr[i] = Integer.parseInt(strArr[i]);
        }
        return intArr;
    }


    /**
     * 全层数组深拷贝,
     *
     * @param arr 拷贝后的数组
     * @param <T> 数据模板
     * @return 拷贝后的数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] fullClone(final T[] arr) {
        int len = arr.length;
        T[] copy = arr.clone();
        for (int i = 0; i < len; i++) {
            T t = copy[i];
            if (t != null && t.getClass().isArray()) {
                copy[i] = (T) fullClone((Object[]) t);
            }
        }
        return copy;
    }

    /**
     * @param arr 二维数组
     * @return 深拷贝的二维数组
     */
    public static int[][] deepClone(final int[][] arr) {
        int len = arr.length;
        int[][] copy = arr.clone();
        for (int i = 0; i < len; i++) {
            copy[i] = arr[i].clone();
        }
        return copy;
    }

    /**
     * 数组填充, 注意T[]数组中对象必须全部都能够接收 val类型才可以, 否则可能抛出异常
     *
     * @param arr 填充数组
     * @param val 填充值
     * @param <T> 类型
     */
    public static <T> void fullFill(T[] arr, T val) {
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            T t = arr[i];
            if (t != null && t.getClass().isArray()) {
                fullFill((Object[]) t, val);
            } else {
                arr[i] = val;
            }
        }
    }

    /**
     * 查询 n 在 arr 中的位置
     *
     * @param arr 数组
     * @param n   对象
     * @return n 在 arr 中的位置
     */
    public static int indexOf(Object[] arr, Object n) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == n) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 查询 n 在 arr 中的位置
     *
     * @param arr 数组
     * @param n   整型值
     * @return n 在 arr 中的位置
     */
    public static int indexOf(int[] arr, int n) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == n) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param start    开始
     * @param end      结束
     * @param interval 间隔
     * @return start 开始到 end 结束(未必会有 end ), 间隔为 interval 的数组
     */
    public static int[] getIntervalArr(int start, int end, int interval) {
        int len = (end - start) / interval;
        if (len < 0) {
            throw new IllegalArgumentException(String.format("start: %s -> end: %s 间隔不能为 : %s", start, end, interval));
        }
        if (len == 0) {
            int[] arr = new int[1];
            arr[0] = start;
            return arr;
        }
        int[] result = new int[len];
        for (int i = 0, n = start; i < len; i++, n += interval) {
            result[i] = n;
        }
        return result;
    }

    /**
     * 判断数组是否所有元素全为null
     *
     * @param array 待判断的数组
     * @param <T>   数组类型
     * @return true: 或数组元素全为null, false: 数组中有值存在
     */
    public static <T> boolean isEmpty(@NonNull T[] array) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 操作数组切面, 有效操作范围是 [0, size)
     * <p>
     * 过滤掉数组中不符合 predicate 函数的对象, 并将有效切面范围内对象前移, 返回新的数组大小
     * </p>
     * <p>
     * <br><b>eg: </b>
     * <br><b>原始数组: </b>  collect = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
     * <br><b>处理代码: </b> ArrayUtils.filter(collect, 5, n -> n % 2 == 0);
     * <br><b>之后数组: </b>  [0, 2, 4, null, null, 5, 6, 7, 8, 9]
     * <br><b>返回值: </b>  3
     * </p>
     *
     * @param objects   数组
     * @param size      大小
     * @param predicate 过滤函数
     * @param <E>       类型
     * @return 新的数组有效大小
     */
    public static <E> int filter(final E[] objects, final int size, final Predicate<? super E> predicate) {
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (predicate.test(objects[i])) {
                if (i != j) {
                    objects[j] = objects[i];
                    objects[i] = null;
                }
                j++;
            } else {
                objects[i] = null;
            }
        }
        return j;
    }

    /**
     * 将一个列表分割成最多指定指定数量个列表
     * <p>
     * 若 count > list.size() , 则分割成 list.size() 个列表
     * <p>
     * <br> eg: 源列表 [0, 1, 2, 3, 4]
     * <br> <b>分成 1 个: </b> [[0, 1, 2, 3, 4]]
     * <br> <b>分成 2 个: </b> [[0, 1, 2], [3, 4]]
     * <br> <b>分成 3 个: </b> [[0, 1], [2, 3], [4]]
     * <br> <b>分成 4 个: </b> [[0], [1], [2], [3], [4]]
     * <br> <b>分成 5 0信1息2信息3 个: </b> [[0], [1], [2], [3], [4]]
     * </p>
     *
     * @param list  列表
     * @param count 风格数量
     * @param <T>   列表中对象类型
     * @return 分割后的列表
     */
    @SuppressWarnings("java:S127")
    public static <T> List<List<T>> partition(List<T> list, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count should not letter than 0");
        }
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        if (count == 1) {
            return Collections.singletonList(list);
        }
        final int i = list.size() / count;
        final int j = list.size() % count;
        final int len = i <= 0 ? j : count;
        final List<List<T>> pList = new ArrayList<>(len);
        for (int k = 0, s = 0, e; k < len; k++) {
            e = s + i + (k < j ? 1 : 0);
            pList.add(list.subList(s, e));
            s = e;
        }
        return pList;
    }

}
