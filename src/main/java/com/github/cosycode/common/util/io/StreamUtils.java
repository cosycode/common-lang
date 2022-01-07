package com.github.cosycode.common.util.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.LongConsumer;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2022/1/7
 * </p>
 *
 * @author CPF
 * @since 1.6
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class StreamUtils {

    /**
     * 从输入流转向输出流
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     * @param longCallback 回调接口, 回调流传输的byte进度
     */
    public static void streamTransfer(InputStream inputStream, OutputStream outputStream, LongConsumer longCallback) throws IOException {
        // 根据实际运行效果 设置缓冲区大小
        final int cache = 8 * 1024;
        byte[] buffer = new byte[cache];
        long porcess = 0;
        int ch;
        while ((ch = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, ch);
            porcess += ch;
            if (longCallback != null) {
                longCallback.accept(porcess);
            }
        }
    }

}
