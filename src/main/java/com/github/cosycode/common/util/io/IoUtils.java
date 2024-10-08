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
 * <b>Description : </b> Io 工具类
 * <p>
 * <b>created in </b> 2019/8/28
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class IoUtils {

    private IoUtils() {
    }

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
                log.error("ioUtils close stream error1", e);
            }
        }
        return flag;
    }


    /**
     * @param outputStream 输出流
     * @param string       传入的数据
     * @throws IOException 写入到流中的异常
     */
    public static void writeStringToOutputStream(@NonNull OutputStream outputStream, @NonNull String string) throws IOException {
        writeStringToOutputStream(outputStream, string, null);
    }

    /**
     * 将字符串写入流
     *
     * @param outputStream 输出流
     * @param string       传入的数据
     * @param charset      编码
     * @throws IOException 写入到流中的异常
     */
    public static void writeStringToOutputStream(@NonNull OutputStream outputStream, @NonNull String string, Charset charset) throws IOException {
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
     * @throws IOException 读取流数据异常
     */
    public static String readStringFromInputStream(InputStream inputStream) throws IOException {
        return readStringFromInputStream(inputStream, null);
    }

    /**
     * 输入流转String
     * <p>
     * 利用 InputStreamReader 和 char[] 读取流
     *
     * @param inputStream 输入流
     * @param charset     编码
     * @return 转换的字符串
     * @throws IOException 读取流数据异常
     */
    public static String readStringFromInputStream(InputStream inputStream, Charset charset) throws IOException {
        try (InputStreamReader inputStreamReader = charset == null ?
                new InputStreamReader(inputStream) : new InputStreamReader(inputStream, charset)) {
            char[] chars = new char[8 * 1024];
            int read;
            //字符串缓冲区
            StringBuilder stringBuilder = new StringBuilder();
            //按行读
            while ((read = inputStreamReader.read(chars)) > 0) {
                //追加到字符串缓冲区存放
                stringBuilder.append(chars, 0, read);
            }
            //将字符串返回
            return stringBuilder.toString();
        }
    }

    /**
     * 按行读取流, 由 consumer 对行进行消费
     * <p>
     * 利用 InputStreamReader 和 BufferedReader 读取流, 并按行进行消费处理
     *
     * @param inputStream 输入流
     * @param charset     编码
     * @param rowConsumer 按行读取流的消费函数
     * @throws IOException 读取流数据异常
     */
    public static void readAndConsumeRowFromInputStream(InputStream inputStream, Charset charset, Consumer<String> rowConsumer) throws IOException {
        try (InputStreamReader inputStreamReader = charset == null ?
                new InputStreamReader(inputStream) : new InputStreamReader(inputStream, charset)) {
            //字符缓冲流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String len;
            //按行读
            while ((len = bufferedReader.readLine()) != null) {
                //追加到字符串缓冲区存放
                rowConsumer.accept(len);
            }
        }
    }

    /**
     * 读取文件
     *
     * @param file 待处理的文件
     * @return 从文件中读取的字符串
     * @throws IOException 文件读取失败异常
     */
    public static String readFile(final File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("not found the file: " + file.getPath());
        }
        // 读出来的长度是文件所占据的内存字节大小
        final long length = file.length();
        char[] chars = new char[(int) length];
        try (final FileReader reader = new FileReader(file)) {
            // 由于有中文的关系, read 和 length 会不一致, read 是占用 char 的数量, length 是占据内存字节数量
            // 例如, 大多数汉字使用一个 char 就可以表示, 只有少数符号和汉字使用两个char
            final int read = reader.read(chars);
            log.info("read success length " + read);
            return new String(chars, 0, read);
        } catch (IOException e) {
            throw new IOException("failed to read the file: filePath", e);
        }
    }

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @return 从文件中读取的字符串
     * @throws IOException 文件读取失败异常
     */
    public static String readFile(final String filePath) throws IOException {
        final File file = new File(filePath);
        return readFile(file);
    }

    /**
     * 读取文件
     *
     * @param file 待处理的文件
     * @return 从文件中读取的字符串
     * @throws IOException 文件读取失败异常
     */
    public static byte[] readFileToByteArr(final String file) throws IOException {
        // 读出来的长度是文件所占据的内存字节大小
        final long length = file.length();
        byte[] chars = new byte[(int) length];
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            // 由于有中文的关系, read 和 length 会不一致, read 是占用 char 的数量, length 是占据内存字节数量
            // 例如, 大多数汉字使用一个 char 就可以表示, 只有少数符号和汉字使用两个char
            final int read = fileInputStream.read(chars);
            log.info("read success length " + read);
            return chars;
        } catch (IOException e) {
            throw new IOException("failed to read the file: filePath", e);
        }
    }

    /**
     * 往 savePath 路径 写入文件, 如果没有则新增
     *
     * @param savePath 写入路径
     * @param content  内容
     * @throws FileNotFoundException 文件未发现异常
     * @throws IOException           写入流异常
     */
    public static void writeFile(@NonNull String savePath, @NonNull byte[] content) throws IOException {
        final File file = new File(savePath);
        FileSystemUtils.insureFileExist(file);

        // 写入文件
        try (final FileOutputStream writer = new FileOutputStream(file)) {
            writer.write(content);
        }
    }

    /**
     * 文件拷贝
     *
     * @param sourceFilePath 源文件路径
     * @param savePath       存储路径
     * @throws IOException 文件写入异常
     */
    public static void copyFile(String sourceFilePath, String savePath) throws IOException {
        try (FileInputStream in = new FileInputStream(sourceFilePath); FileOutputStream out = new FileOutputStream(savePath)) {
            byte[] buf = new byte[8 * 1024];
            int bytes;
            while ((bytes = in.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, bytes);
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new IOException("failed to copy file ==> readPath: " + sourceFilePath + ", writePath: " + savePath, e);
        }
    }

    /**
     * 以字节流的形式 读取资源文件
     *
     * @param classPath 源文件路径
     * @throws IOException 文件写入异常
     * @return 字节流文件的字节数组
     */
    public static byte[] readFromResource(@NonNull ClassLoader classLoader, String classPath) throws IOException {
        try (InputStream in = classLoader.getResourceAsStream(classPath)) {
            if (in == null) {
                throw new FileNotFoundException(classPath);
            }
            return readByteArrayFromResource(in);
        }
    }

    /**
     * 从输入流里读取 动态字节流 文件(适用于无法获取文件大小)
     *
     * @param inputStream 输入流
     * @throws IOException 文件写入异常
     * @return 字节流文件的字节数组
     */
    public static byte[] readByteArrayFromResource(@NonNull InputStream inputStream) throws IOException {
        byte[] buf = new byte[8 * 1024];
        // 动态字节流
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        int len;
        while ((len = inputStream.read(buf)) != -1) {
            arrayOutputStream.write(buf, 0, len);
        }
        return arrayOutputStream.toByteArray();
    }

    /**
     * 往 savePath 路径 写入文件, 如果没有则新增
     *
     * @param image 图片
     * @param file  写入文件的路径
     */
    public static void savePic(Image image, @NonNull final File file) {
        final String fileName = file.getName();
        savePic(image, file, fileName.substring(fileName.lastIndexOf('.') + 1));
    }

    /**
     * 往 savePath 路径 写入文件, 如果没有则新增
     *
     * @param image      图片
     * @param file       文件
     * @param formatName 文件格式
     */
    public static void savePic(Image image, @NonNull final File file, @NonNull String formatName) {
        Validate.isTrue(StringUtils.isNoneBlank(formatName), "formatName cannot be empty");

        FileSystemUtils.insureFileExist(file);

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
