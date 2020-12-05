package com.github.cosycode.common.util.common;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/6/15 16:26
 */
@Slf4j
public class ObjUtils {

    private ObjUtils(){}

    /**
     * 判断两个对象是否相等, 两个对象均为 null 也算相等
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
                log.error("date parse error: while convert {} to Date.", str);
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
     */
    public static byte[] objectToByte(@NonNull Serializable obj) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
             ObjectOutputStream oo = new ObjectOutputStream(bo)){
            oo.writeObject(obj);
            bytes = bo.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 将byte[]反序列化回java对象
     */
    public static <T extends Serializable> T byteToObject(@NonNull byte[] bytes, @NonNull Class<T> cls) {
        try (ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
             ObjectInputStream oi = new ObjectInputStream(bi)){
            return cls.cast(oi.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
