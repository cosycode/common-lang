package com.github.cosycode.common.util.reflex;

import com.github.cosycode.common.base.FileDisposer;
import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.util.io.FileSystemUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * <b>Description : </b> 包工具类
 * <p>
 * 功能1: 获取并处理一个包下的所有类
 * <p>
 * <b>created in </b> 2020/6/29
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
public class PackageUtils {

    public static final String CLASS_SUFFIX = ".class";

    private PackageUtils() {
    }

    /**
     * 获取 clazz 所在 package 中的 经过 filter 过滤后的 class 对象的集合
     *
     * @param loader 类加载器, 加载包的时候一定要选对类加载器, 一般在没有自定义加载器的情况下, 同一个 module 下的类加载器是同一个.
     * @param clazz  类
     * @param filter 过滤器
     * @return clazz 所在 package 中的 经过 filter过滤后的 class 对象
     * @throws FileNotFoundException 通过class获取class所在jar包
     * @throws IOException           读取`通过class获取class所在jar包`出现IO异常
     */
    public static List<Class<?>> getClassesFromJar(ClassLoader loader, Class<?> clazz, Predicate<JarEntry> filter) throws IOException {
        if (clazz == null) {
            return Collections.emptyList();
        }
        // 获取 jarPath
        final String jarPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        // 获取 clazz 所在包路径
        String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);
        // jarFile
        final File file1 = new File(jarPath);
        if (!file1.exists()) {
            throw new FileNotFoundException(String.format("jarPath: %s doesn't exist, clazz: %s, packagePath: %s", clazz.getName(), jarPath, packagePath));
        }
        try (JarFile file = new JarFile(file1)) {
            return file.stream()
                    .filter(jarEntry -> jarEntry.getName().startsWith(packagePath) && (filter == null || filter.test(jarEntry)))
                    .map(jarEntry -> {
                        String className = jarEntry.getName().replace(CLASS_SUFFIX, "").replace(File.separatorChar, '.');
                        return Throws.sup(() -> Class.forName(className, false, loader))
                                .runtimeExp(String.format("failed to load class: %s, please check if the selection (%s) of classLoader is correct.", className, loader.getClass()))
                                .value();
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException(String.format("failed to get class from jar, clazz: %s, jarPath: %s, packagePath: %s", clazz.getName(), jarPath, packagePath), e);
        }
    }

    /**
     * 获取某个包下的所有类(获取jar包中类可能会出错)
     *
     * @param loader         类加载器, 加载包的时候一定要选对类加载器, 一般在没有自定义加载器的情况下, 同一个 module 下的类加载器是同一个.
     * @param packageName    包名: eg: com.github.common 或 com/Github/common
     * @param loadSubPackage 是否加载子包
     * @param loadInnerClass 是否加载内部类
     * @return 加载的类Set集合
     * @throws IOException 读取`通过class获取class所在jar包`出现IO异常
     */
    public static Set<Class<?>> getClassesFromPackage(ClassLoader loader, final String packageName, final boolean loadSubPackage, final boolean loadInnerClass) throws IOException {
        Set<Class<?>> classSet = new HashSet<>();
        final String packagePrefix = packageName.replace(".", File.separator);
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packagePrefix);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url == null) {
                continue;
            }
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
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
                FileFilter fileFilter = file -> {
                    if (file.isFile() && file.getName().endsWith(CLASS_SUFFIX)) {
                        return loadInnerClass || !file.getName().contains("$");
                    }
                    return loadSubPackage && file.isDirectory();
                };
                for (File file : array) {
                    FileSystemUtils.fileDisposeFromDir(file, disposer, fileFilter);
                }
            } else if ("jar".equals(protocol)) {
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                if (jarURLConnection != null) {
                    try (JarFile jarFile = jarURLConnection.getJarFile()) {
                        int packagePrefixLength = packagePrefix.length();
                        // 该 jarFile 里面包含了其中所有的文件夹和所有类(包括内部类)
                        jarFile.stream().filter(jarEntry -> {
                                    String name = jarEntry.getName();
                                    if (!name.startsWith(packagePrefix)) {
                                        return false;
                                    }
                                    if (!name.endsWith(CLASS_SUFFIX)) {
                                        return false;
                                    }
                                    // 排除子类
                                    if (!loadSubPackage && name.indexOf(".", packagePrefixLength) < name.length() - 6) {
                                        return false;
                                    }
                                    return loadSubPackage || !name.contains("$");
                                })
                                .forEach(jarEntry -> {
                                    String jarEntryName = jarEntry.getName();
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf('.')).replace("/", ".");
                                    Class<?> aClass = Throws.sup(() -> Class.forName(className, false, loader))
                                            .runtimeExp(String.format("failed to load class: %s, please check if the selection (%s) of classLoader is correct.", className, loader.getClass()))
                                            .value();
                                    classSet.add(aClass);
                                });
                    }
                }
            }
        }
        return classSet;
    }

}