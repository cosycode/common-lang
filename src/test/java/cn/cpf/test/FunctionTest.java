package cn.cpf.test;

import com.github.cosycode.common.ext.hub.LazySingleton;
import com.github.cosycode.common.thread.CtrlLoopThread;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * date 2020/11/20 11:22
 **/
public class FunctionTest {


    static BigInteger g = new BigInteger("29");
    static int dd = 29;

    @Test
    public void main() throws InterruptedException {
        final CtrlLoopThread build = CtrlLoopThread.builder().continueIfException(false).millisecond(50).booleanSupplier(() -> {
            dd = dd - 1;
            final int dds = dd;
            System.out.println(28 / dds);
            return true;
        }).build();
        build.startOrWake();
        System.out.println("pause0`");
        Thread.sleep(100);
        System.out.println("pause`");
        build.startOrWake();
        Thread.sleep(100);
        System.out.println("pause1");
        build.wake();
        Thread.sleep(100);
        System.out.println("pause2");
        build.pause();
        Thread.sleep(100);
        System.out.println("pause3");
        build.startOrWake();
        Thread.sleep(100);
        System.out.println("pause4");
    }


    final LazySingleton<Object> singleton = LazySingleton.of(Object::new);

    @Test
    public void testLazy() {
        // 单例工具对象

        // 多次输出单例对象地址
        System.out.println(singleton.instance());
        System.out.println(singleton.instance());
        System.out.println(singleton.instance());

        // 破坏单例
        singleton.destroy();
        System.out.println(singleton.instance());
        System.out.println(singleton.instance());
        System.out.println(singleton.instance());
    }

    @Test
    public void testLazy1() {
        // ArrayBlockingQueue(10000) 很占用内存, 可以作为懒加载形式
        final LazySingleton<BlockingQueue<String>> singleton = LazySingleton.of(() -> {
            System.out.println("创建一个ArrayBlockingQueue, 大小为10000");
            return new ArrayBlockingQueue<>(10000);
        });

        // 多次输出单例对象地址
        System.out.println(singleton.instance().hashCode());
        System.out.println(singleton.instance().hashCode());

        // 破坏单例
        singleton.destroy();
        System.out.println(singleton.instance().hashCode());
        System.out.println(singleton.instance().hashCode());
    }


}
