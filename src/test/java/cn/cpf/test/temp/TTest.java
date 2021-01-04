package cn.cpf.test.temp;

import cn.cpf.test.temp.TemplateClazz.*;
import com.github.cosycode.common.util.common.CollectUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @date 2020/12/11 14:19
 **/
public class TTest {

    public static BlackApple testBlackApple(BlackApple b) {
        System.out.println(b);
        return b;
    }

    public static <T extends BlackApple> T testBlackApple2(T b) {
        System.out.println(b);
        return b;
    }

    @Test
    public void test1() {
        List<Apple> apples = new ArrayList<>();
        apples.add(new BlackApple());
        final Apple apple = apples.get(1);
    }

    @Test
    public void test2() {
        // 引用表示引用的可能是已存在的东西,
        List<? extends BigBlackApple> bigBlackApples = new ArrayList<>();
        List<? extends WhiteApple> whiteApples = new ArrayList<>();
        List<? extends Apple> apples = whiteApples;
        apples = bigBlackApples;
//        apples.add(new BlackApple());
        final Apple apple = apples.get(1);
    }

    @Test
    public void test22() {
        // 引用表示引用的可能是已存在的东西,
        List<BigBlackApple> bigBlackApples = new ArrayList<>();
        List<? extends WhiteApple> whiteApples = new ArrayList<>();
        // error
//        List<TemplateClazz> apples = (List<TemplateClazz>) whiteApples;
//        apples.add(new BlackApple());
//
//        apples.forEach(System.out::println);
    }

    @Test
    public void test3() {
        // 引用表示引用的可能是已存在的东西,
        List<? super Apple> bigBlackApples = new ArrayList<>();
        List<? super Fruit> whiteApples = new ArrayList<>();
        List<? super BlackApple> apples = bigBlackApples;
        apples.add(new BigBlackApple());
        final Object object = apples.get(1);
    }

    @Test
    public void test4() {
        // 引用表示引用的可能是已存在的东西,
        List<? super Apple> bigBlackApples = new ArrayList<>();
        List<? super Fruit> whiteApples = new ArrayList<>();
        List<? super BlackApple> apples = bigBlackApples;
        apples.add(new BigBlackApple());
        final Object object = apples.get(1);
    }

    @Test
    public void testClass1() {
        // 引用表示引用的可能是已存在的东西,
        Class<BigBlackApple> bigBlackApples = BigBlackApple.class;
        Class<WhiteApple> whiteApples = WhiteApple.class;
        // error
//        Class<TemplateClazz> apples = whiteApples;
    }

    @Test
    public void testClass2() {
        // 引用表示引用的可能是已存在的东西,
        Class<BigBlackApple> bigBlackApples = BigBlackApple.class;
        Class<? extends BlackApple> whiteApples1 = BlackApple.class;
        Class<? extends BlackApple> whiteApples2 = SmallBlackApple.class;
        // error
//        Class<Apple> apples = whiteApples2;
//        System.out.println(apples);
    }

    @Test
    public void testClass5() {
        // 引用表示引用的可能是已存在的东西,
        List<? super Apple> bigBlackApples = new ArrayList<>();
        List<? super Fruit> whiteApples = new ArrayList<>();
        List<? super BlackApple> apples = bigBlackApples;
        apples.add(new BlackApple());
        final Object object = apples.get(1);
    }

    @Test
    public void test134(){
        final ThreadLocal<SimpleFormatter> simpleFormatterThreadLocal = ThreadLocal.withInitial(SimpleFormatter::new);
        final Set<SimpleFormatter> collect = IntStream.range(0, 1000).parallel().mapToObj(it -> simpleFormatterThreadLocal.get()).collect(Collectors.toSet());
        System.out.println(collect);


    }


}
