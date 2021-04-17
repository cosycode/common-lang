package cn.cpf.test.proxy;

import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.ext.proxy.LogCallExecuteProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.*;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/7
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class FunctionTest {

    /**
     * 当前方法在同一时间内只能够有一个线程执行业务逻辑
     */
    private static void fun01() {
        log.info("call start {}", Thread.currentThread().getId());
        {
            // ---- 业务逻辑(用睡眠10 ms 表示) ----
            Throws.con(RandomUtils.nextInt(50, 200), Thread::sleep);
        }
        log.info("call start {}", Thread.currentThread().getId());
    }

    @Test
    public void testProxyForFun01() {
        final Runnable proxy = new LogCallExecuteProxy<>((Runnable) FunctionTest::fun01).proxy();
        proxy.run();
    }

    public static void fun02(int i) {
        log.info("call fun2");
    }

    @Test
    public void testProxyForFun02() {
        final Consumer<Integer> proxy = new LogCallExecuteProxy<>((Consumer<Integer>) FunctionTest::fun02).proxy();
        proxy.accept(400);
    }

    public void fun03(Object[] objects) {
        log.info("call fun2");
    }

    @Test
    public void testProxyForFun03() {
        Consumer<Object[]> fun03 = new FunctionTest()::fun03;
        Consumer<Object[]> proxy = new LogCallExecuteProxy<>(fun03).proxy();
        proxy.accept(new Object[]{"haa", 400, new ArrayList<>()});
    }

    public String fun11() {
        log.info("call fun11");
        return UUID.randomUUID().toString();
    }

    @Test
    public void testProxyForFun11() {
        Supplier<String> fun11 = new FunctionTest()::fun11;
        Supplier<String> proxy = new LogCallExecuteProxy<>(fun11).proxy();
        final String result = proxy.get();
        System.out.println(result);
    }

    public String fun12(String str) {
        log.info("call fun12");
        return str + new Random().nextLong();
    }

    @Test
    public void testProxyForFun12() {
        Function<String, String> fun12 = new FunctionTest()::fun12;
        Function<String, String> proxy = new LogCallExecuteProxy<>(fun12).proxy();
        final String result = proxy.apply("lalalala");
        System.out.println(result);
    }


    public void fun13(String str, Map<String, Object> map) {
        log.info("call fun13");
    }

    @Test
    public void testProxyForFun13() {
        BiConsumer<String, Map<String, Object>> biConsumer = new FunctionTest()::fun13;

        final BiConsumer<String, Map<String, Object>> proxy = new LogCallExecuteProxy<>(biConsumer,
                (BiConsumer<String, Map<String, Object>> t, Object[] params) -> t.accept((String) params[0], (Map<String, Object>) params[1])
        ).proxy(t -> (String s, Map<String, Object> map) -> t.apply(new Object[]{s, map}));

        Map<String, Object> map = new HashMap<>();
        map.put("haha", LocalDate.now());
        map.put("hehe", LocalTime.now());

        proxy.accept("gaga", map);
    }


    public String fun14(String str, Map<String, Object> map) {
        log.info("call fun14");
        return str + " -------- " + map.toString();
    }

    @Test
    public void testProxyForFun14() {
        BiFunction<String, Map<String, Object>,String> biConsumer = new FunctionTest()::fun14;

        final BiFunction<String, Map<String, Object>,String> proxy = new LogCallExecuteProxy<>(biConsumer,
                (BiFunction<String, Map<String, Object>,String> t, Object[] params) -> t.apply((String) params[0], (Map<String, Object>) params[1])
        ).proxy(t -> (String s, Map<String, Object> map) -> t.apply(new Object[]{s, map}));

        Map<String, Object> map = new HashMap<>();
        map.put("haha", LocalDate.now());
        map.put("hehe", LocalTime.now());

        final String gaga = proxy.apply("gaga", map);
        System.out.println(gaga);
    }



    public Map<Integer, String> fun24(String str, boolean flag, Integer... integers) {
        log.info("call fun2");
        if (flag) {
            return null;
        }
        Map<Integer, String> map = new HashMap<>();
        if (integers != null) {
            Arrays.stream(integers).forEach(i -> map.put(i, str));
        }
        return map;
    }

    @FunctionalInterface
    interface Function24 {
        Map<Integer, String> fun24(String str, boolean flag, Integer... integers);
    }

    @Test
    public void testProxyForFun241() {
        Function24 function24 = new FunctionTest()::fun24;
        final Function24 proxy = new LogCallExecuteProxy<>(function24,
                (Function24 t, Object[] params) -> t.fun24((String) params[0], (boolean) params[1], (Integer[]) params[2])
        ).proxy(t -> (String str, boolean flag, Integer... integers) -> t.apply(new Object[]{str, flag, integers}));

        final Map<Integer, String> map1 = proxy.fun24("haha", false, 1, 2, 4, 6, 7, 8);
        System.out.println(map1);
        final Map<Integer, String> map2 = proxy.fun24("haha", true, 1, 2, 4, 6, 7, 8);
        System.out.println(map2);
    }
}
