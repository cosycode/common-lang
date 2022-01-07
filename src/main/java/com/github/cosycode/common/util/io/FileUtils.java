package com.github.cosycode.common.util.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/1/7
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    /**
     * 更改文件内容
     *
     * @param file 文件
     * @param operator 操作方式, 若返回数据为 null 则表示, 不进行替换
     */
    public static void modifyFileContent(File file, UnaryOperator<String> operator) throws IOException {
        final String s = IoUtils.readFile(file);
        final String apply = operator.apply(s);
        if (apply == null) {
            return;
        }
        IoUtils.writeStringToOutputStream(new FileOutputStream(file), apply);
    }

}
