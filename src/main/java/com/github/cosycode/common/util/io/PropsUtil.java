package com.github.cosycode.common.util.io;

import com.github.cosycode.common.util.common.BeanUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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

    /**
     * 加载文件
     *
     * @param filename 资源文件路径及文件名
     * @return 加载文件之后创建的实例对象
     * @throws IOException 文件加载失败异常
     */
    public static Properties loadProps(@NonNull String filename) throws IOException {
        return loadProps(filename, StandardCharsets.UTF_8);
    }

    /**
     * 加载 properties文件
     *
     * @param propsFile properties文件对象
     * @param charset   按照指定文件格式加载 Properties 文件
     * @return 加载文件之后创建的实例对象
     * @throws IOException 文件加载失败异常
     */
    public static Properties loadProps(@NonNull File propsFile, Charset charset) throws IOException {
        try (InputStream is = new FileInputStream(propsFile)) {
            Properties props = new Properties();
            try (final InputStreamReader inputStreamReader = new InputStreamReader(is, charset)) {
                props.load(inputStreamReader);
            }
            return props;
        } catch (IOException e) {
            throw new IOException(propsFile.getAbsolutePath() + " file load failure", e);
        }
    }

    /**
     * 加载 Properties 文件
     * <p>
     * 路径1: 前面有 classpath: 表示资源文件路径下的文件
     * 路径2: 绝对路径
     * 路径3: 相对路径, 以当前项目文件位 BasePath
     * </P>
     *
     * @param filePath 文件路径
     * @param charset  按照指定文件格式加载 Properties 文件
     * @return 加载文件之后创建的实例对象
     * @throws IOException 文件加载失败异常
     */
    public static Properties loadProps(@NonNull String filePath, Charset charset) throws IOException {
        final File file = FileSystemUtils.findFile(filePath);
        if (file == null) {
            throw new IllegalArgumentException("文件路径识别失败: " + filePath);
        }
        if (!file.exists()) {
            throw new FileNotFoundException("文件参数: " + filePath + ", 未发现文件: " + file.getCanonicalPath());
        }
        if (!file.canRead()) {
            throw new IOException("文件没有读取权限 ==> 文件参数: " + filePath + ", 读取文件: " + file.getCanonicalPath());
        }
        return loadProps(file, charset);
    }

    /**
     * 从 filename 中提取的键为 key 的属性, 如果为空, 则返回 defaultValue
     *
     * @param filename     properties文件名
     * @param key          键值
     * @param defaultValue 获取失败的默认值.
     * @return 从 filename 中提取的键为 key 的字符串, 如果为空, 则返回 defaultValue
     * @throws IOException 文件加载失败异常
     */
    public static String getString(String filename, String key, String defaultValue) throws IOException {
        final Properties properties = loadProps(filename, StandardCharsets.UTF_8);
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 获取属性
     *
     * @param filename properties文件名
     * @param key      键.
     * @return 从 filename 中提取的键为 key 的字符串
     * @throws IOException 文件加载失败异常
     */
    public static String getString(String filename, String key) throws IOException {
        return getString(filename, key, "");
    }

    /**
     * 将文件属性映射到 bean 中
     *
     * @param properties properties
     * @param bean       待装载的bean
     * @param <T>        bean的类型
     */
    public static <T> T populateToBean(@NonNull T bean, @NonNull Properties properties, String prefix) {
        final Map<String, Object> map = new HashMap<>();
        prefix = prefix.trim();
        if (prefix.isEmpty()) {
            properties.forEach((ok, ov) -> map.put(ok.toString(), ov));
        } else {
            final String prefixPoint = prefix + '.';
            final int splitLen = prefixPoint.length();
            properties.forEach((ok, ov) -> {
                final String key = ok.toString();
                if (key.length() <= splitLen || !key.startsWith(prefixPoint)) {
                    return;
                }
                map.put(key.substring(splitLen), ov);
            });
        }
        BeanUtils.populate(bean, map);
        return bean;
    }


}
