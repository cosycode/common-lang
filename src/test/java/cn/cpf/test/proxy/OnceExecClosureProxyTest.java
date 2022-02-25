package cn.cpf.test.proxy;

import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.ext.proxy.OnceExecClosureProxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
public class OnceExecClosureProxyTest {

    private void onClick(String msg) {
        log.info("start {}", msg);

        // ---- 业务逻辑可能有点长(用睡眠10 ms 表示) ----
        Throws.con(10, Thread::sleep);

        log.info(" end  {}", msg);
    }

    @Test
    public void onClickTest1() {
        IntStream.range(0, 5).parallel().forEach(num -> {
            onClick(" time: " + num);
            log.info("--------------- 线程 {} 调用完成", num);
        });
    }

    @Test
    public void onClickTest2() {
        Consumer<String> consumer = OnceExecutorForConsumer.of(this::onClick);

        IntStream.range(0, 10).parallel().forEach(num -> {
            consumer.accept(" time: " + num);
            log.info("--------------- 线程 {} 调用完成", num);
        });
    }

    /**
     * 当前方法在同一时间内只能够有一个线程执行业务逻辑
     */
    private void runDemo(String msg) {
        log.info("start {}", msg);
        {
            // ---- 业务逻辑(用睡眠10 ms 表示) ----
            Throws.con(10, Thread::sleep);
        }
        log.info(" end  {}", msg);
    }

    /**
     * 普通情况下 开启8个线程执行
     */
    @Test
    public void test0() {
        OnceExecClosureProxyTest click = new OnceExecClosureProxyTest();
        // 正常情况下开启 8 个线程执行
        IntStream.range(0, 8).parallel().mapToObj(Integer::toString).forEach(click::runDemo);
    }

    /**
     * 经过 OnceExecClosureProxy 代理类后 开启8个线程执行
     * 如果同一时间多个线程执行代理方法, 则将会只有一个线程能够执行被代理方法, 其它线程直接跳过
     */
    @Test
    public void test1() {
        OnceExecClosureProxyTest click = new OnceExecClosureProxyTest();
        // 对 OnceExecClosureProxyTest::test 方法代理后 开启8个线程运行
        IntStream.range(0, 8).parallel().mapToObj(Integer::toString).forEach(OnceExecClosureProxy.of(click::runDemo));
    }

    /**
     * 经过 OnceExecClosureProxy 代理类后 开启8个线程执行
     * 如果同一时间多个线程执行代理方法, 则将会只有一个线程能够执行被代理方法, 其它线程 执行skip 方法
     */
    @Test
    public void test2() {
        OnceExecClosureProxyTest click = new OnceExecClosureProxyTest();
        // 对 OnceExecClosureProxyTest::test 方法代理后 开启8个线程运行
        IntStream.range(0, 8).parallel().mapToObj(Integer::toString).forEach(
                new OnceExecClosureProxy<>((Consumer<String>)click::runDemo).skip(id -> log.debug("id {} 跳过", id)).proxy()
        );
    }

}
