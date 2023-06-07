package com.github.cosycode.common.util.common;

import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.lang.RuntimeExtException;
import com.github.cosycode.common.lang.ShouldNotHappenException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <b>Description : </b> Bean 工具类
 * <p>
 * <b>created in </b> 2020/6/15
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
public class BeanUtils {

    private BeanUtils() {
    }

    /**
     * 生成从 target 中获取 Field 的 Function&lt;String, Field&gt;
     * Function:String =&gt; 字段名称
     * Function:Field =&gt; 字段
     * eg: 生成 Function 之后通过 Function.apply(name), 既可获取 Target 对象中的 name 字段, 若Target 对象中没有name字段, 则返回 null
     *
     * @param targetFields 反射的对象字段
     * @return Function&lt;String, Field&gt;
     */
    public static Function<String, Field> geneGetFieldFunction(Field[] targetFields) {
        if (targetFields.length == 0) {
            return s -> null;
        }
        // 定义从targetFields获取指定Field的Function
        if (targetFields.length > 8) {
            // 将 targetFields 转换为 map, 利用map中的get方法获取指定 Field
            final Map<String, Field> collect = Arrays.stream(targetFields).collect(Collectors.toMap(Field::getName, it -> it));
            return collect::get;
        } else {
            // for 循环形式从 targetFields 中获取指定 Field
            return s -> {
                for (Field field : targetFields) {
                    if (field.getName().equals(s)) {
                        return field;
                    }
                }
                return null;
            };
        }
    }


    /**
     * 将 source 中的属性 拷贝到 target 中
     *
     * @param target 目标 pojo 对象
     * @param source 源 pojo 对象
     */
    @SuppressWarnings("java:S3011")
    public static void copyProperties(@NonNull Object target, @NonNull Object source) {
        // 初始化
        final Field[] targetFields = target.getClass().getDeclaredFields();
        if (targetFields.length == 0) {
            return;
        }
        final Field[] sourceFields = source.getClass().getDeclaredFields();
        if (sourceFields.length == 0) {
            return;
        }
        // 定义从targetFields获取指定Field的Function
        Function<String, Field> function = geneGetFieldFunction(targetFields);
        // 遍历 sourceFields 从中获取方法, 并赋值
        for (Field sourceField : sourceFields) {
            final Field apply = function.apply(sourceField.getName());
            if (apply != null && apply.getType().equals(sourceField.getType())) {
                apply.setAccessible(true);
                sourceField.setAccessible(true);
                try {
                    apply.set(target, sourceField.get(source));
                } catch (IllegalAccessException e) {
                    throw new ShouldNotHappenException(e);
                }
            }
        }
    }

    /**
     * 将 source 中的属性 拷贝到 target 中
     *
     * @param target 目标 pojo 对象
     * @param source 源map对象
     */
    @SuppressWarnings({"java:S3011", "java:S135"})
    public static void populate(@NonNull Object target, @NonNull Map<String, Object> source) {
        // 初始化
        if (source.isEmpty()) {
            return;
        }
        final Field[] targetFields = target.getClass().getDeclaredFields();
        if (targetFields.length == 0) {
            return;
        }
        // 定义从targetFields获取指定Field的Function
        Function<String, Field> function = geneGetFieldFunction(targetFields);
        // 遍历 sourceFields 从中获取方法, 并赋值
        for (Map.Entry<String, Object> sEntry : source.entrySet()) {
            final Field tField = function.apply(sEntry.getKey());
            if (tField == null) {
                continue;
            }
            final Object sValue = sEntry.getValue();
            if (sValue == null) {
                continue;
            }
            final Class<?> type = tField.getType();
            Object value = TypeConvertUtils.convert(sValue, type);
            if (value == null) {
                log.debug("类型不匹配! target: [{}], source: [{}@{}]", type, sValue.getClass(), sValue);
                continue;
            }
            tField.setAccessible(true);
            try {
                tField.set(target, value);
            } catch (IllegalAccessException e) {
                throw new ShouldNotHappenException(e);
            }
        }
    }


    /**
     * 对象转换为 Map&lt;String, Object&gt; 输出
     *
     * @param pojo 待转换的bean
     * @param <T>  bean的类型
     * @return 转换后的Map对象
     */
    public static <T> Map<String, Object> pojoToStringObjectMap(T pojo) {
        if (pojo == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        Throws.runtimeEpt(() -> {
            BeanInfo beanInfo = Introspector.getBeanInfo(pojo.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                Method getter = property.getReadMethod();
                if (getter != null && !"class".equals(property.getName())) {
                    Object value = getter.invoke(pojo);
                    if (value != null) {
                        map.put(property.getName(), value);
                    }
                }
            }
        });
        return map;
    }

    public static <T> T mapToBean(Map<String, Object> properties, Class<T> beanClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        T t;
        try {
            t = beanClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeExtException(beanClass.getName() + "中未发现无参构造方法", e);
        }
        populate(t, properties);
        return t;
    }

    public static <T> List<T> mapListToBeanList(List<Map<String, Object>> mapList, Class<T> beanClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        List<T> result = new ArrayList<>();
        if (mapList == null) {
            return result;
        }
        for (Map<String, Object> map : mapList) {
            result.add(mapToBean(map, beanClass));
        }
        return result;
    }

}
