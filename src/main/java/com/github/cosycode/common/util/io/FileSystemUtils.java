package com.github.cosycode.common.util.io;

import com.github.cosycode.common.base.FileDisposer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;

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

}
