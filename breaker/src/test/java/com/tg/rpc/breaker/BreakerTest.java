package com.tg.rpc.breaker;

import com.tg.rpc.breaker.concurrent.task.CommonTask;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/25.
 */
public class BreakerTest {
    public static void main(String[] args) throws Throwable {
        BreakerProperty breakerProperty = new BreakerProperty().addClass("com.tg.rpc.breaker.TestServiceIface");
        Breaker breaker = new Breaker(breakerProperty);
        Method metricsMethod = TestServiceIface.class.getMethod("test", String.class, int.class);
        Object obj = new TestServiceIfaceImpl();
        for (int i = 0; i < 1000000; i++) {
            CommonTask task;
            if (i > 300) {
                task = new CommonTask(metricsMethod, new Object[]{"twogoods", 2}, obj, 90l);
            } else {
                task = new CommonTask(metricsMethod, new Object[]{"twogoods", i % 3}, obj, 90l);
            }
            Thread.sleep(90L);
            try {
                breaker.execute(task);
            } catch (Exception e) {
            }
        }
    }
}