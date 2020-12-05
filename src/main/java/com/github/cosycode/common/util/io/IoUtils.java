package com.github.cosycode.common.util.io;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @since 1.0
 * @date 2019/8/28 10:35
 **/
@Slf4j
public class IoUtils {

    private IoUtils(){}

    /**
     * 关闭流
     *
     * @param closeableList 带关闭的io列表
     * @return 是否关闭成功
     */
    public static boolean close(Closeable... closeableList) {
        boolean flag = true;
        for (Closeable closeable : closeableList) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                flag = false;
                log.error("ioUtils close stream error1" , e);
            }
        }
        return flag;
    }


    /**
     *
     * @param outputStream 输出流
     * @param string        传入的数据
     */
    public static void writeStringToOutputStream(@NonNull OutputStream outputStream, @NonNull String string) throws IOException {
        writeStringToOutputStream(outputStream, string, null);
    }

    /**
     * 将字符串写入流
     *
     * @param outputStream 输出流
     * @param string        传入的数据
     * @param charset     编码
     */
    public static void writeStringToOutputStream(@NonNull OutputStream outputStream, @NonNull String string, Charset charset) throws IOException {
        string = string.trim();
        if ("".equals(string)) {
            return;
        }
        // 传输
        try (OutputStreamWriter out = charset == null ?
                new OutputStreamWriter(outputStream) : new OutputStreamWriter(outputStream, charset)) {
            out.write(string);
            out.flush();
        }
    }

    /**
     * 输入流转String
     *
     * @param inputStream 输入流
     * @return 转换的字符串
     */
    public static String readStringFromInputStream(InputStream inputStream) throws IOException {
        return readStringFromInputStream(inputStream, null);
    }

    /**
     * 输入流转String
     *
     * @param inputStream 输入流
     * @param charset 编码
     * @return 转换的字符串
     */
    public static String readStringFromInputStream(InputStream inputStream, Charset charset) throws IOException {
        try (InputStreamReader inputStreamReader = charset == null ?
                new InputStreamReader(inputStream) : new InputStreamReader(inputStream, charset)) {
            //字符缓冲流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //字符串缓冲区
            StringBuilder stringBuilder = new StringBuilder();
            String len;
            //按行读
            while((len=bufferedReader.readLine()) != null){
                //追加到字符串缓冲区存放
                stringBuilder.append(len);
            }
            //将字符串返回
            return stringBuilder.toString();
        }
    }

    /**
     * 输入流转String
     *
     * @param inputStream 输入流
     * @param charset 编码
     */
    public static void readStringFromInputStream(InputStream inputStream, Charset charset, Consumer<String> consumer) throws IOException {
        try (InputStreamReader inputStreamReader = charset == null ?
                new InputStreamReader(inputStream) : new InputStreamReader(inputStream, charset)) {
            //字符缓冲流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String len;
            //按行读
            while((len=bufferedReader.readLine()) != null){
                //追加到字符串缓冲区存放
                consumer.accept(len);
            }
        }
    }

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     *
     * @return 文件字符串
     */
    public static String readFile(String filePath) {
        final File file = new File(filePath);
        Validate.isTrue(file.exists(), "文件不存在");
        final long length = file.length();
        char[] chars = new char[(int)length];
        try (final FileReader reader = new FileReader(file)){
            final int read = reader.read(chars);
            log.info("read success length " + read);
        } catch (IOException e) {
            log.error("读取文件失败", e);
        }
        return new String(chars);
    }

    /**
     * 往 savePath 路径 写入文件, 如果没有则新增
     *
     * @param savePath 写入路径
     * @param content 内容
     *
     */
    public static void writeFile(@NonNull String savePath, @NonNull byte[] content) throws IOException {
        final File file = new File(savePath);
        insureFileExist(file);

        // 写入文件
        try (final FileOutputStream writer = new FileOutputStream(file)){
            writer.write(content);
        }
    }


    /**
     * 文件拷贝
     *
     * @param sourceFilePath 源文件路径
     * @param savePath 存储路径
     */
    public static void copyFile(String sourceFilePath, String savePath) throws IOException {
        try (FileInputStream in = new FileInputStream(sourceFilePath); FileOutputStream out = new FileOutputStream(savePath)) {
            byte[] buf = new byte[8 * 1024];
            int bytes;
            while ((bytes = in.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, bytes);
            }
        }
    }


    /**
     * 确保文件夹存在, 不存在则创建文件夹
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
     * 往 savePath 路径 写入文件, 如果没有则新增
     *
     * @param image    图片
     * @param file 写入文件的路径
     */
    public static void savePic(Image image, @NonNull final File file) {
        final String fileName = file.getName();
        savePic(image, file, fileName.substring(fileName.lastIndexOf('.') + 1));
    }

    /**
     * 往 savePath 路径 写入文件, 如果没有则新增
     *
     * @param image    图片
     * @param file       文件
     * @param formatName 文件格式
     */
    public static void savePic(Image image, @NonNull final File file, @NonNull String formatName) {
        Validate.isTrue(StringUtils.isNoneBlank(formatName), "formatName为空, 不知道该转换成什么图片格式");

        insureFileExist(file);

        int w = image.getWidth(null);
        int h = image.getHeight(null);
        //首先创建一个BufferedImage变量，因为ImageIO写图片用到了BufferedImage变量。
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

        //再创建一个Graphics变量，用来画出来要保持的图片，及上面传递过来的Image变量
        Graphics g = bi.getGraphics();
        try {
            g.drawImage(image, 0, 0, null);
            //将BufferedImage变量写入文件中。
            ImageIO.write(bi, formatName, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 释放图形对象
        g.dispose();
    }
}
