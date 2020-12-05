package com.github.cosycode.common.util.reflex;

import com.github.cosycode.common.base.FileDisposer;
import com.github.cosycode.common.util.io.FileSystemUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * <b>Description : </b> 包工具类
 * 功能1: 获取并处理一个包下的所有类
 *
 * @author CPF
 * @since 1.0
 * @date 2020/6/29 10:38
 */
@Slf4j
public class PackageUtils {

    private PackageUtils(){}

    public static final String CLASS_SUFFIX = ".class";

    public static void disposeClassesFromJar(Class<?> clazz, Predicate<JarEntry> predicate, Consumer<Class<?>> classConsumer) {
        List<Class<?>> classFromJar = getClassesFromJar(clazz, predicate);
        classFromJar.forEach(classConsumer);
    }

    /**
     * 获取 clazz 所在 package 中的 经过 filter过滤后的 class 对象
     *
     * @param clazz 类
     * @param filter 过滤器
     * @return clazz 所在 package 中的 经过 filter过滤后的 class 对象
     */
    public static List<Class<?>> getClassesFromJar(Class<?> clazz, Predicate<JarEntry> filter) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        // 获取 jarPath
        final String jarPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        // 获取 clazz 所在包路径
        String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);

        try (JarFile file = new JarFile(new File(jarPath))) {
            return file.stream()
                    .filter(jarEntry -> jarEntry.getName().startsWith(packagePath) && (filter == null || filter.test(jarEntry)))
                    .map(jarEntry -> ClassUtils.loadClass(jarEntry.getName().replace(CLASS_SUFFIX, "").replace(File.separatorChar, '.')))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("获取路径出错", e);
        }
        return Collections.emptyList();
    }

    /**
     * 获取某个包下的所有类(获取jar包中类可能会出错)
     *
     * @param packageName 包名: eg: com.github.common 或 com/github/common
     */
    public static Set<Class<?>> getClassesFromPackage(String packageName, boolean loadChildren) throws IOException {
        Set<Class<?>> classSet = new HashSet<>();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", File.separator));
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url == null) {
                continue;
            }
            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                String packagePath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8.name());
                File file1 = new File(packagePath);
                final String packagePath0 = file1.getPath();

                // 获取包下的文件
                File[] array = Optional.ofNullable(file1.listFiles()).orElse(new File[]{});

                FileDisposer disposer = file -> {
                    String classPath = file.getPath().replace(CLASS_SUFFIX, "").replace(packagePath0, packageName).replaceAll("[/\\\\]", ".");
                    Class<?> aClass = ClassUtils.loadClass(classPath, false);
                    classSet.add(aClass);
                };
                // 如果loadChildren为false, 则只获取包下class文件
                FileFilter fileFilter = file -> (file.isFile() && file.getName().endsWith(CLASS_SUFFIX)) || (loadChildren && file.isDirectory());
                Arrays.stream(array).forEach(it -> FileSystemUtils.fileDisposeFromDir(it, disposer, fileFilter));
            } else if (protocol.equals("jar")) {
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                if (jarURLConnection != null) {
                    try (JarFile jarFile = jarURLConnection.getJarFile()) {
                        jarFile.stream().filter(jarEntry -> jarEntry.getName().endsWith(CLASS_SUFFIX))
                                .forEach(jarEntry -> {
                                    String jarEntryName = jarEntry.getName();
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf('.')).replace("/", ".");
                                    Class<?> aClass = ClassUtils.loadClass(className);
                                    classSet.add(aClass);
                                });
                    }
                }
            }
        }
        return classSet;
    }

}