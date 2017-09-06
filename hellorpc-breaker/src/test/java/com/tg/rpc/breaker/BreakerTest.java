package com.tg.rpc.breaker;

import com.tg.rpc.breaker.concurrent.task.HookTask;
import com.tg.rpc.breaker.concurrent.task.ReflectTask;
import com.tg.rpc.breaker.concurrent.task.TaskExecuteHook;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/27.
 */
public class BreakerTest {
    BreakerProperty breakerProperty = new BreakerProperty().addClass("com.tg.rpc.breaker.TestServiceIface");
    Breaker breaker = new Breaker(breakerProperty);

    @Test
    public void testHook() throws Throwable {
        //Hook利用函数式的方式传递实现，方法参数个数上很难实现灵活，Hook方式现在只支持一个参数的方法
        TestServiceIface testServiceIface = new TestServiceIfaceImpl();
        TaskExecuteHook<String, String> taskExecuteHook = testServiceIface::echo;
        Method metricsMethod = TestServiceIface.class.getMethod("echo", String.class);
        Object res = breaker.execute(new HookTask<>(taskExecuteHook, "twogoods", () -> {
            return new Object[]{"twogoods"};
        }, metricsMethod));
        System.out.println(res);
    }

    @Test
    public void testReflect() throws Throwable {
        Method metricsMethod = TestServiceIface.class.getMethod("test", String.class, int.class);
        Object obj = new TestServiceIfaceImpl();
        ReflectTask task = new ReflectTask(metricsMethod, new Object[]{"twogoods", 2}, obj, 90l);
        Object res = breaker.execute(task);
        System.out.println(res);
    }
}