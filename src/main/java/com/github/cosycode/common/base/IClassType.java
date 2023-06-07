package com.github.cosycode.common.base;

import java.lang.reflect.ParameterizedType;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/5/16
 * </p>
 *
 * @author CPF
 * @since 1.9
 **/
public interface IClassType<T> {

    /**
     * 获取 Class 对象中的第一个泛型类 Class 对象.
     * <p>
     * 要求当前实例的 class 对象是一个 带有泛型的对象. 一般有如下两种方式.
     * <br>1, 子类对象带有泛型 T, 之后可以通过泛型 T 获取 对应的 class 对象.
     * <br>2, 创建实例的时候, 后面要加大括号, 表示创建一个匿名子类 : new Animal&lt;Cat&gt;(){}, 这种子类对象的临时class 对象带有泛型, 可以通过此方法获取到该泛型的class 对象.
     * </p>
     *
     * @return 子类 class 的第一个泛型类型
     */
    @SuppressWarnings("unchecked")
    default Class<T> classType() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
