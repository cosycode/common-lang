package com.github.cosycode.common.util.encode;

import com.github.cosycode.common.ext.hub.SimpleCode;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * <b>Description : </b> 信息编码工具类
 * <p>
 * <b>created in </b> 2020/5/14
 *
 * @author CPF
 * @since 1.0
 **/
public class MessageDigestUtils {

    private MessageDigestUtils() {
    }

    /**
     * 获取一个文件的md5值(可处理大文件)
     *
     * @param file 待计算的文件
     * @return md5 value 文件的MD5值
     * @throws java.io.FileNotFoundException file文件未发现异常
     * @throws IOException                   读取文件异常
     */
    public static String getMD5(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            MessageDigest md5 = SimpleCode.runtimeException(() -> MessageDigest.getInstance("MD5"));
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            byte[] digest = md5.digest();
            return new BigInteger(1, digest).toString(16);
        }
    }

    /**
     * 获取一个byte[]的md5值(可处理大文件)
     *
     * @param data 待转换的数据
     * @return md5 value 文件的MD5值
     */
    public static String getMD5(byte[] data) {
        MessageDigest md5 = SimpleCode.runtimeException(() -> MessageDigest.getInstance("MD5"));
        byte[] digest = md5.digest(data);
        return new BigInteger(1, digest).toString(16);
    }

    /**
     * 将中文字符转换为对应的 Unicode 编码
     *
     * @param gbString 中文字符
     * @return 中文字符对应的 Unicode 编码
     */
    public static String encodeUnicodeString(String gbString) {
        if (StringUtils.isBlank(gbString)) {
            return "";
        }
        char[] utfBytes = gbString.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            sb.append("\\u");
            if (hexB.length() <= 2) {
                sb.append("00");
            }
            sb.append(hexB);
        }
        return sb.toString();
    }

    /**
     * 将 Unicode 编码字符串转换为对应字符字符(例如: 中文字符)
     * <p>
     *     系统中接受中文参数变成百分号，如：“黄大”-->“%u9EC4%u5927”
     *     而实际上内容对应，应该是：“黄大”-->“\u9EC4\u5927”，中文变unicode
     * </p>
     *
     * @param unicodeString Unicode 编码字符串
     * @return 转换成字符的字符串
     */
    public static String decodeUnicodeString(String unicodeString) {
        unicodeString = unicodeString.replace("%","\\");//这行酌情不要
        int start = 0;
        int end = 0;
        final StringBuilder stringBuilder = new StringBuilder();
        while (start > -1) {
            end = unicodeString.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = unicodeString.substring(start + 2);
            } else {
                charStr = unicodeString.substring(start + 2, end);
            }
            // 16进制parse整形字符串。
            char letter = (char) Integer.parseInt(charStr, 16);
            stringBuilder.append(letter);
            start = end;
        }
        return stringBuilder.toString();
    }

}
