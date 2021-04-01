package com.github.cosycode.common.helper;

import com.github.cosycode.common.util.io.FileSystemUtils;
import lombok.Getter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * <b>Description : </b> 该类的作用是为了防止应用多开而做的
 * <p> APP启动后, 执行此类相关方法, 锁定一个文件, 直至APP关闭后文件被JVM释放.
 * <b>created in </b> 2021/1/20
 *
 * @author CPF
 * @since 1.2
 **/
public final class AppLockHelper {

    @Getter
    private static final Set<AppLockHelper> fileLockOccupySet = new HashSet<>(2);

    public static void lockFile(String fileName) throws IOException {
        fileLockOccupySet.add(new AppLockHelper(fileName));
    }

    /**
     * 文件路径（最好使用一个绝对路径），保证使用不同的JVM，不同副本的同一个执行项目的文件路径均相同
     */
    public final String fileName;

    /**
     * 文件锁
     */
    public final FileLock fileLock;

    /**
     * 锁定指定文件, 如果文件不存在则创建文件后并锁定文件
     *
     * @param fileName 文件绝对路径
     * @throws IOException 文件异常
     */
    @SuppressWarnings("java:S2095")
    private AppLockHelper(String fileName) throws IOException {
        this.fileName = fileName;
        //获得实例标志文件
        File flagFile = new File(fileName);
        final boolean create = FileSystemUtils.insureFileExist(flagFile);
        // 此处不要关闭流，否则无法实现锁定文件的功能
        FileOutputStream fos = new FileOutputStream(flagFile, true);
        this.fileLock = fos.getChannel().tryLock();
        if (create) {
            fos.write("这是一个应用锁文件,目的是防止应用多开,应用启动后会锁定这个文件,如果应用第二次开启,则会因为获取不到这个文件的锁而报错!\n".getBytes(StandardCharsets.UTF_8));
        }
        final String txt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + " : 启动了程序\n";
        fos.write(txt.getBytes(StandardCharsets.UTF_8));
        fos.flush();
        //返回空表示文件已被运行的实例锁定
        if (fileLock == null) {
            throw new FileSystemException("未获取到文件锁, 请确认程序是否打开了两次：" + fileName);
        }
    }

    public static String getJarPath() {
        return new File(AppLockHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().trim();
    }

}
