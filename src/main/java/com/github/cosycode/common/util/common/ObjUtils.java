package com.github.cosycode.common.util.common;

import com.github.cosycode.common.lang.ActionExecException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * <b>Description : </b> 对象的工具类
 * <p>
 * <b>created in </b> 2020/6/15
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
public class ObjUtils {

    private ObjUtils() {
    }

    /**
     * 判断两个对象是否相等, 两个对象均为 null 也算相等
     *
     * @param obj1 待比较的对象1
     * @param obj2 待比较的对象2
     * @return 是否相等
     */
    public static boolean isEqualWithNullAble(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        } else {
            return obj1.equals(obj2);
        }
    }

    /**
     * String 转换为指定class类型
     *
     * @param str   将String转换为 T 类型
     * @param clazz T的class
     * @param <T>   指定转换的类型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(String str, Class<T> clazz) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        if (String.class.equals(clazz)) {
            return (T) str;
        }
        Object o = null;
        if (clazz == Date.class) {
            try {
                o = DateUtils.parseDate("yyyy-MM-dd HH:mm:ss", "yyyyMMdd", "yyyyMMdd HH:mm:ss", "yyyy/MM/dd HH:mm:ss");
            } catch (ParseException e) {
                throw new ActionExecException("date parse error: while convert " + str + " to Date.", e);
            }
        } else if (clazz == BigDecimal.class) {
            o = new BigDecimal(str);
        } else if (clazz == Long.class) {
            o = Long.valueOf(str);
        } else if (clazz == Integer.class) {
            o = Integer.valueOf(str);
        } else if (clazz == int.class) {
            o = Integer.parseInt(str);
        } else if (clazz == float.class) {
            o = Float.parseFloat(str);
        } else if (clazz == boolean.class) {
            o = Boolean.parseBoolean(str);
        } else if (clazz == byte.class) {
            o = Byte.parseByte(str);
        }
        return (T) o;
    }

    /**
     * 将对象序列化为 byte[]
     *
     * @param obj 待序列化的对象
     * @return 转换后的byte数组
     */
    public static byte[] objectToByte(@NonNull Serializable obj) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
             ObjectOutputStream oo = new ObjectOutputStream(bo)) {
            oo.writeObject(obj);
            bytes = bo.toByteArray();
        } catch (IOException e) {
            throw new ActionExecException("failed to convert to object to byte[]: " + obj, e);
        }
        return bytes;
    }

    /**
     * 将byte[]反序列化回java对象
     *
     * @param bytes 待反序列化的byte数组
     * @param cls   指定转换的class类型
     * @param <T>   指定转换的类型
     * @return 转换后的对象
     */
    public static <T extends Serializable> T byteToObject(@NonNull byte[] bytes, @NonNull Class<T> cls) {
        try (ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
             ObjectInputStream oi = new ObjectInputStream(bi)) {
            return cls.cast(oi.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
