package com.github.cosycode.common.ext.mapping;

/**
 * <b>Description : </b> IMapping&lt;String, String&gt;映射接口, 用于从枚举种获取对象
 * <p>
 * <b>created in </b> 2020/6/4
 *
 * @author CPF
 * @see com.github.cosycode.common.ext.mapping.IMapping
 * @since 1.0
 */
public interface IStringStringMapping extends IMapping<String, String> {

    /**
     * 通过key来获取enumClass中匹配的枚举对象
     *
     * @param enumClass 枚举类
     * @param key       代码
     * @param <T>       模板类型
     * @return 如果 enumClass为空, 返回 null, 否则返回枚举类中第一个匹配 key 的枚举对象.
     */
    static <T extends IStringStringMapping> T getByKey(Class<T> enumClass, String key) {
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
     * @param <T>       模板类型
     * @return 如果 enumClass为空, 返回 null, 否则返回枚举类中第一个匹配 val 的枚举对象.
     */
    static <T extends IStringStringMapping> T getByVal(Class<T> enumClass, String val) {
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
     * @param <T>       模板类型
     * @return 如果 key为空
     */
    static <T extends IStringStringMapping> String getValByKey(Class<T> enumClass, String key) {
        if (key == null) {
            return null;
        }
        IStringStringMapping bean = getByKey(enumClass, key);
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
     * @return 如果 key为空
     */
    static <T extends IStringStringMapping> String getKeyByVal(Class<T> enumClass, String val) {
        if (val == null) {
            return null;
        }
        IStringStringMapping bean = getByVal(enumClass, val);
        if (null == bean) {
            return null;
        }
        return bean.key();
    }

}
