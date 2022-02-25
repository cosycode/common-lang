# common-lang

A more powerful tool class based on `org.apache.commons:commons-lang3`

个人封装的一些类库

## repo

最新1.6版本

1. Apache Maven

    ```xml
    <dependency>
       <groupId>com.github.cosycode</groupId>
       <artifactId>common-lang</artifactId>
       <version>1.6</version>
    </dependency>
    ```

2. gradle
    
    ```yaml
    implementation 'com.github.cosycode:common-lang:1.6'
    ```

## 闭包代理

一种针对方法(函数式接口)的代理方式

文档详见

- CSDN: <https://blog.csdn.net/u011511756/article/details/115682437>
- gitee: <https://cpfree.gitee.io/cpfree/#/pblog/design-code/closure-proxy/闭包代理模式-初创篇.blog>
- github: <https://cpfree.github.io/cpfree/#/pblog/design-code/closure-proxy/闭包代理模式-初创篇.blog>

`common-lang` 里面已经实现的代理类

| 类                       | 作用                                                                                                                |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------- |
| SingletonClosureProxy    | 将一个原本不是单例的方法 在经过代理后变成单例方法.                                                                  |
| OnceExecClosureProxy     | 使得在多个线程在调用同一个方法时, 同一时间只有一个能够执行, 其它的则执行 skip 方法, 如果 skip 方法为空, 则直接跳过. |
| CurrentLimitClosureProxy | 经它代理过的方法(函数式接口实例)在同一时间内只能够允许几个线程运行, 其它的则阻塞等待.                               |
| LogCallExecuteProxy      | 在被代理类调用的前后, 打印出调用的参数和返回的结果, 以及运行的时间等信息                                            |
| CacheClosureProxy        | 经过它处理的方法, 参数和返回值会被缓存起来, 再次调用直接从缓存里面去取                                              |

## 类分享

1. 单例模式处理类 `com.github.cosycode.common.ext.hub.LazySingleton`

   - CSDN: <https://blog.csdn.net/u011511756/article/details/123037785>
   - gitee: <https://cpfree.gitee.io/cpfree/#/pblog/design-code/cosycode/singleton.blog>
   - github: <https://cpfree.github.io/cpfree/#/pblog/design-code/cosycode/singleton.blog>

2. 可控制的单循环线程 `com.github.cosycode.common.thread.CtrlLoopThreadComp`

   - CSDN: <https://blog.csdn.net/u011511756/article/details/123037785>
   - gitee: <https://cpfree.gitee.io/cpfree/#/pblog/design-code/cosycode/singleton.blog>
   - github: <https://cpfree.github.io/cpfree/#/pblog/design-code/cosycode/singleton.blog>

3. Throws
   平时放在测试代码里面还是挺爽的.

   - CSDN: <https://blog.csdn.net/u011511756/article/details/115436503>
   - gitee: <https://cpfree.gitee.io/cpfree/#/pblog/design-code/分享两个项目中不让用但在私下用的很爽的异常处理方式.design>
   - github: <https://cpfree.github.io/cpfree/#/pblog/design-code/分享两个项目中不让用但在私下用的很爽的异常处理方式.design>

4. AsynchronousProcessor: 异步处理器

5. CommandLineHelper: 命令行参数 helper

6. AppLockHelper: 文件锁

......

## 一些工具类

| 类              | 描述                                                          |
| --------------- | ------------------------------------------------------------- |
| IoUtils         | io 操作                                                       |
| FileSystemUtils | 文件工具类, 系统搜索和调用文件                                |
| TestUtils       | 测试工具类                                                    |
| PropsUtil       | properties 文件处理类                                         |
| StrUtils        | 字符串工具类, 包含正则替换, 正则搜索, Camel 字符串转换等      |
| ArrUtils        | 矩阵操作, 数组计算, 数组分割, 过滤, 多维数组内容填充和深拷贝, |
| TestUtils       | 测试工具类, 打印一个函数的执行时长, 循环 n 次计算执行消耗时间 |
| PrintTool       | 简单切换 log 和 `System.out.println`                          |

......
