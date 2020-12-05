package com.github.cosycode.common.util.reflex;

/**
 * <b>Description : </b> 类相关的工具类
 *
 * @author CPF
 * @since 1.0
 * @date 2020/6/22 13:33
 */
public class ClassUtils {

    private ClassUtils(){}

    /**
     * 加载类并返回类class对象
     * @param className     加载类
     * @param isInitialized 是否进行初始化操作
     * @return 类class对象
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 加载类（默认不初始化类）
     */
    public static Class<?> loadClass(String className) {
        return loadClass(className, false);
    }


}