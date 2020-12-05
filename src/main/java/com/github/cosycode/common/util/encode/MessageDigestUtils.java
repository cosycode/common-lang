package com.github.cosycode.common.util.encode;

import com.github.cosycode.common.ext.hub.SimpleCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * <b>Description : </b> 字符串扩展处理类
 *
 * @author CPF
 * @since 1.0
 * @date 2019/10/10 11:48
 **/
public class MessageDigestUtils {

    private MessageDigestUtils() {}

    /**
     * 获取一个文件的md5值(可处理大文件)
     *
     * @return md5 value
     */
    public static String getMD5(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            MessageDigest md5 = SimpleCode.runtimeExceptionForSup(() -> MessageDigest.getInstance("MD5"));
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            byte[] digest = md5.digest();
            return new BigInteger(1, digest).toString(16);
        }
    }

    public static String getMD5(byte[] data) {
        MessageDigest md5 = SimpleCode.runtimeExceptionForSup(() -> MessageDigest.getInstance("MD5"));
        byte[] digest = md5.digest(data);
        return new BigInteger(1, digest).toString(16);
    }

}
