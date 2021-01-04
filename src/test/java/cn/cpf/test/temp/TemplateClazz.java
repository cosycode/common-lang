package cn.cpf.test.temp;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @date 2020/12/11 15:47
 **/
public class TemplateClazz{

    interface Food { }
    interface Good { }

    interface Meet extends Food, Good { }
    interface Fruit extends Food, Good { }

    static class Beef implements Meet { }

    static class Apple implements Fruit { }
    static class Banana implements Fruit { }
    static class Pair implements Fruit { }

    static class BlackApple extends Apple { }
    static class WhiteApple extends Apple { }

    static class BigBlackApple extends BlackApple { }
    static class SmallBlackApple extends BlackApple { }

//    public static void main(String[] args) {
//        {
//            List<Apple> apples1 = new ArrayList<>();
//            List<? extends Apple> apples2 = new ArrayList<>();
//            List<BlackApple> apples3 = new ArrayList<>();
//            List<? extends BlackApple> apples4 = new ArrayList<>();
//
//            apples1 = apples2;
//            apples1 = apples3;
//            apples1 = apples4;
//
//            apples2 = apples1;
//            apples2 = apples3;
//            apples2 = apples4;
//
//            apples3 = apples1;
//            apples3 = apples2;
//            apples3 = apples4;
//
//            apples4 = apples1;
//            apples4 = apples2;
//            apples4 = apples3;
//        }
//        {
//            List<Apple> apples1 = new ArrayList<>();
//            List<? super Apple> apples2 = new ArrayList<>();
//            List<BlackApple> apples3 = new ArrayList<>();
//            List<? super BlackApple> apples4 = new ArrayList<>();
//
//            apples1 = apples2;
//            apples1 = apples3;
//            apples1 = apples4;
//
//            apples2 = apples1;
//            apples2 = apples3;
//            apples2 = apples4;
//
//            apples3 = apples1;
//            apples3 = apples2;
//            apples3 = apples4;
//
//            apples4 = apples1;
//            apples4 = apples2;
//            apples4 = apples3;
//        }
//
//        {
//            List<Apple> apples01 = new ArrayList<>();
//            List<? extends Apple> apples02 = new ArrayList<>();
//            List<? super Apple> apples03 = new ArrayList<>();
//            List<BlackApple> apples11 = new ArrayList<>();
//            List<? extends BlackApple> apples12 = new ArrayList<>();
//            List<? super BlackApple> apples13 = new ArrayList<>();
//
//            apples02 = apples01;
//            apples02 = apples11;
//            apples02 = apples12;
//
//            apples13 = apples01;
//            apples13 = apples11;
//            apples13 = apples03;
//        }
//
//    }


}
