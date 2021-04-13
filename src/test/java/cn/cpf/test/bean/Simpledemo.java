package cn.cpf.test.bean;

import com.github.cosycode.common.ext.hub.Throws;

import java.util.Date;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/1
 *
 * @author CPF
 * @since
 **/
public class Simpledemo {
    static volatile boolean done = true;

    public static void main(String[] args) {
        testWithAwaitility();
    }

    private static void testWithAwaitility() {
        System.out.println("start " + new Date());
        new Thread(() -> {
            Throws.run(() -> Thread.sleep(50000));
            Throws.con(50000, Thread::sleep);
            done = true;
        }).start();


        try {
            await().atMost(2, SECONDS).until((Callable) () -> done);
        } catch (Exception e) {
            System.out.println("FAILED");
            e.printStackTrace();
        }

        System.out.println("end " + new Date());   // REACHED this statement after correction
    }
}