package com.github.cosycode.common.util.io;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * <b>Description : </b> properties 文件处理工具类
 * <p>
 * 1. 加载资源文件
 * 2. 加载绝对路径
 * <p>
 * 01. 动态加载
 * 02. 反复利用
 * <p>
 * <b>created in </b> 2019/8/28
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
public final class PropsUtil {

    private PropsUtil() {
    }

    private static final Properties NULL_PROPS = new Properties();

    /**
     * 加载文件
     *
     * @param filename 资源文件路径及文件名
     * @return 加载文件之后创建的实例对象
     */
    public static Properties loadProps(@NonNull String filename) {
        return loadProps(filename, Charset.defaultCharset());
    }

    /**
     * 加载文件
     *
     * @param filename 资源文件路径及文件名
     * @param charset  按照指定文件格式加载 Properties 文件
     * @return 加载文件之后创建的实例对象
     */
    public static Properties loadProps(@NonNull String filename, Charset charset) {
        Properties props = null;
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new FileNotFoundException(filename + " file not found.");
            }
            props = new Properties();
            try (final InputStreamReader inputStreamReader = new InputStreamReader(is, charset)) {
                props.load(inputStreamReader);
            }
        } catch (IOException e) {
            log.error(filename + " file load failure", e);
        }
        return props == null ? NULL_PROPS : props;
    }

    /**
     * 从 filename 中提取的键为 key 的属性, 如果为空, 则返回 defaultValue
     *
     * @param filename     properties文件名
     * @param key          键值
     * @param defaultValue 获取失败的默认值.
     * @return 从 filename 中提取的键为 key 的字符串, 如果为空, 则返回 defaultValue
     */
    public static String getString(String filename, String key, String defaultValue) {
        return loadProps(filename).getProperty(key, defaultValue);
    }

    /**
     * 获取属性
     *
     * @param filename properties文件名
     * @param key      键.
     * @return 从 filename 中提取的键为 key 的字符串
     */
    public static String getString(String filename, String key) {
        return loadProps(filename).getProperty(key);
    }

}
