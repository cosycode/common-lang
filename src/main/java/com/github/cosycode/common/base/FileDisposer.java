package com.github.cosycode.common.base;

import java.io.File;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2020/5/12
 */
@FunctionalInterface
public interface FileDisposer {

    void dispose(File file);

}
