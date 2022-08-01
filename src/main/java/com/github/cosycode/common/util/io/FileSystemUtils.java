package com.github.cosycode.common.util.io;

import com.github.cosycode.common.base.FileDisposer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * <b>Description : </b> 文件系统工具类
 * <p>
 * <b>created in </b> 2020/5/12
 *
 * @author CPF
 * @since 1.0
 */
@Slf4j
public class FileSystemUtils {

    private FileSystemUtils() {
    }

    /**
     * 文件夹递归处理
     * 如果 file 不能通过 fileFilter 过滤条件, 则直接返回.
     * 如果 file 是一个文件, 则执行 fileDisposer 处理方法.
     * 如果 file 是一个文件夹, 则对文件夹中的每个子文件夹和文件递归调用本方法.
     *
     * @param file         文件 或 文件夹.
     * @param fileDisposer 文件处理方式.
     * @param fileFilter   文件过滤器.
     */
    public static void fileDisposeFromDir(File file, FileDisposer fileDisposer, FileFilter fileFilter) {
        fileDisposeByRecursion(file, fileDisposer, fileFilter);
    }

    /**
     * 文件夹递归处理
     * 如果 file 不能通过 fileFilter 过滤条件, 则直接返回.
     * 如果 file 是一个文件, 则执行 fileDisposer 处理方法.
     * 如果 file 是一个文件夹, 则对文件夹中的每个子文件夹和文件递归调用本方法.
     *
     * @param file         文件 或 文件夹.
     * @param fileDisposer 文件处理方式.
     * @param fileFilter   文件过滤器.
     */
    public static void fileDisposeByRecursion(File file, FileDisposer fileDisposer, FileFilter fileFilter) {
        if (file == null || !file.exists()) {
            return;
        }
        // 过滤
        if (fileFilter != null && !fileFilter.accept(file)) {
            return;
        }
        if (file.isFile()) {
            fileDisposer.dispose(file);
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File item : files) {
                    fileDisposeFromDir(item, fileDisposer, fileFilter);
                }
            }
        } else {
            log.debug("非文件或文件夹, 跳过处理! filePath: {}", file.getPath());
        }
    }

    /**
     * 文件夹循环处理
     * 如果 file 不能通过 fileFilter 过滤条件, 则直接返回.
     * 如果 file 是一个文件, 则执行 fileDisposer 处理方法.
     * 如果 file 是一个文件夹, 则对文件夹中的每个子文件夹和文件递归调用本方法.
     *
     * @param file         文件 或 文件夹.
     * @param fileDisposer 文件处理方式.
     * @param fileFilter   文件过滤器.
     */
    public static void fileDisposeByLoop(File file, FileDisposer fileDisposer, FileFilter fileFilter) {
        if (file == null || !file.exists()) {
            return;
        }
        if (fileFilter != null && !fileFilter.accept(file)) {
            return;
        }
        // 以下 file 文件 或 文件夹
        for (LinkedList<File> list = new LinkedList<>(Collections.singletonList(file)); !list.isEmpty(); list.removeFirst()) {
            final File first = list.getFirst();
            if (first.isFile()) {
                fileDisposer.dispose(first);
            } else if (first.isDirectory()) {
                for (File it : Optional.ofNullable(first.listFiles()).orElse(new File[]{})) {
                    if (fileFilter == null || fileFilter.accept(it)) {
                        list.add(it);
                    }
                }
            } else {
                log.debug("非文件或文件夹, 跳过处理! filePath: {}", file.getPath());
                return;
            }
        }
    }

    /**
     * 将根路径里面符合条件的 markdown 文件添加到 list 表, 并返回list
     *
     * @param rootPath   根路径 url
     * @param fileFilter 文件过滤器(null:表示不过滤任何文件)
     * @param loadSubDir 是否加载子文件夹
     * @param consumer   文件消费函数
     */
    public static void findFileFromDir(File rootPath, FileFilter fileFilter, boolean loadSubDir, Consumer<File> consumer) {
        // 获取根路径 url, 并将符合条件的 markdown 文件添加到 list 表
        FileSystemUtils.fileDisposeByRecursion(rootPath, consumer::accept, f -> {
            if (f.isDirectory()) {
                return loadSubDir;
            }
            return fileFilter == null || (f.isFile() && fileFilter.accept(f));
        });
    }

    /**
     * 将根路径里面符合条件的 markdown 文件添加到 list 表, 并返回list
     *
     * @param rootPath 根路径 url
     * @return 根路径里面符合条件的 markdown 文件列表
     */
    public static List<File> findFileFromDir(File rootPath, FileFilter fileFilter, boolean loadSubDir) {
        List<File> list = new ArrayList<>();
        findFileFromDir(rootPath, fileFilter, loadSubDir, list::add);
        return list;
    }

    /**
     * 确保文件夹存在, 不存在则创建文件夹
     *
     * @param dir 文件夹
     */
    public static void insureFileDirExist(@NonNull final File dir) {
        if (dir.exists()) {
            Validate.isTrue(dir.isDirectory(), "创建文件夹异常, 已存在同名非文件夹事物, 请检查 path : " + dir.getPath());
        } else {
            final boolean mkDirs = dir.mkdirs();
            Validate.isTrue(mkDirs, "文件夹创建失败, 请检查 path : " + dir.getPath());
            log.info("文件夹创建成功: {} ", dir);
        }
    }

    /**
     * 确保文件存在
     * 如果不存在则创建文件(包括文件夹)
     *
     * @param file 文件
     * @return 是否创建了文件
     */
    public static boolean insureFileExist(@NonNull final File file) {
        final boolean exists = file.exists();
        if (exists) {
            Validate.isTrue(file.isFile(), "创建文件异常, 已存在同名非文件事物(如存在和当前文件相同的文件夹), 请检查 path : " + file.getPath());
            return false;
        } else {
            // 确保文件所在文件夹存在
            File parentFile = file.getParentFile();
            insureFileDirExist(parentFile);
            // 不存在则创建文件
            try {
                final boolean newFile = file.createNewFile();
                Validate.isTrue(newFile, "文件创建失败 path : " + file.getPath());
                return true;
            } catch (IOException e) {
                log.error("文件创建失败 path : " + file.getPath(), e);
                return false;
            }
        }
    }

    /**
     * 通过文件路径获取正确的文件对象
     *
     * <p>
     * filePathExpression:
     * </p>
     * <p>
     * classpath:{path}: 将获取配置文件的路径
     * </p>
     * <p>
     * suitpath:{path}: 首先从执行路径下找文件, 若找不到再从配置文件中找文件
     * </p>
     *
     * @param filePathExpression 文件路径
     * @return 文件路径对应的文件对象
     */
    public static File findFile(@NonNull String filePathExpression) {
        filePathExpression = filePathExpression.trim();
        if (filePathExpression.startsWith("suitpath:")) {
            final String relativePath = filePathExpression.substring(9);
            File file = findFile(relativePath);
            if (file != null && file.exists()) {
                return file;
            }
            return findFile("classpath:" + relativePath);
        } else if (filePathExpression.startsWith("classpath:")) {
            final String filePath = filePathExpression.substring(10);
            final URL resource = Thread.currentThread().getContextClassLoader().getResource(filePath);
            if (resource == null) {
                return null;
            } else {
                return new File(resource.getPath());
            }
        } else {
            return new File(filePathExpression);
        }
    }

    /**
     * 获取项目的当前工作目录
     *
     * @return System.getProperty(" user.dir ")
     */
    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取用户目录
     *
     * @return System.getProperty(" user.home ")
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }

}
