package com.github.cosycode.common.ext.mapping;

/**
 * <b>Description : </b> Mapping映射接口, 用于从枚举种获取对象
 * <p>
 * <b>created in </b> 2020/3/16
 *
 * @author CPF
 * @since 1.0
 **/
public interface IMapping<K, V> {

    /**
     * 通过key来获取enumClass中匹配的枚举对象
     *
     * @param enumClass 枚举类
     * @param key       代码
     * @param <K1>      待映射 Map 的 key 的类型
     * @param <V1>      待映射 Map 的 value 的类型
     * @param <T>       模板类型
     * @return 如果 enumClass为空, 返回 null, 否则返回枚举类中第一个匹配key的枚举对象
     */
    static <K1, V1, T extends IMapping<K1, V1>> T getByKey(Class<T> enumClass, K1 key) {
        if (enumClass == null) {
            return null;
        }
        //通过反射取出Enum所有常量的属性值
        for (T each : enumClass.getEnumConstants()) {
            //利用key进行循环比较，获取对应的枚举
            if (key.equals(each.key())) {
                return each;
            }
        }
        return null;
    }

    /**
     * 通过val来获取enumClass中匹配的枚举对象
     *
     * @param enumClass 枚举类
     * @param val       值
     * @param <K1>      待映射 Map 的 key 的类型
     * @param <V1>      待映射 Map 的 value 的类型
     * @param <T>       模板类型
     * @return 如果 enumClass为空, 返回 null, 否则返回枚举类中第一个匹配val的枚举对象.
     */
    static <K1, V1, T extends IMapping<K1, V1>> T getByVal(Class<T> enumClass, V1 val) {
        if (enumClass == null) {
            return null;
        }
        //通过反射取出Enum所有常量的属性值
        for (T each : enumClass.getEnumConstants()) {
            //利用key进行循环比较，获取对应的枚举
            if (val.equals(each.val())) {
                return each;
            }
        }
        return null;
    }

    /**
     * 通过 key 来获取 val
     *
     * @param enumClass 枚举类
     * @param key       枚举代码
     * @param <K1>      待映射 Map 的 key 的类型
     * @param <V1>      待映射 Map 的 value 的类型
     * @param <T>       模板类型
     * @return 如果 key为空
     */
    static <K1, V1, T extends IMapping<K1, V1>> V1 getValByKey(Class<T> enumClass, K1 key) {
        if (key == null) {
            return null;
        }
        IMapping<K1, V1> bean = getByKey(enumClass, key);
        if (null == bean) {
            return null;
        }
        return bean.val();
    }

    /**
     * 通过 val 来获取 key
     *
     * @param enumClass 枚举类
     * @param val       枚举代码
     * @param <T>       模板类型
     * @param <K1>      待映射 Map 的 key 的类型
     * @param <V1>      待映射 Map 的 value 的类型
     * @return 如果 key为空
     */
    static <K1, V1, T extends IMapping<K1, V1>> K1 getKeyByVal(Class<T> enumClass, V1 val) {
        if (val == null) {
            return null;
        }
        IMapping<K1, V1> bean = getByVal(enumClass, val);
        if (null == bean) {
            return null;
        }
        return bean.key();
    }

    default K key() {
        return getBean().getKey();
    }

    default V val() {
        return getBean().getVal();
    }

    default MappingBean<K, V> getBean() {
        return MappingBeanPool.get(this);
    }

    default void putBean(K key, V val) {
        MappingBeanPool.put(this, key, val);
    }

}
