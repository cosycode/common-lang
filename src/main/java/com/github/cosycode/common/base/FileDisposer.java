package com.github.cosycode.common.base;

import java.io.File;

/**
 * 文件处理函数式接口
 * <p>
 * <b>created in </b> 2020/5/12
 *
 * @author CPF
 * @since 1.0
 */
@FunctionalInterface
public interface FileDisposer {

    /**
     * @param file 待处理的文件对象
     */
    void dispose(File file);

}
