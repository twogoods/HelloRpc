package com.tg.rpc.breaker;

import com.tg.rpc.breaker.concurrent.task.CommonTask;

/**
 * Created by twogoods on 2017/7/25.
 */
public class BreakerTest {
    public static void main(String[] args) throws Throwable {
        BreakerProperty breakerProperty = new BreakerProperty().addClass("com.tg.rpc.breaker.TestServiceIface");
        Breaker breaker = new Breaker(breakerProperty);
        CommonTask task = new CommonTask(TestServiceIface.class.getMethod("test", String.class, int.class), new Object[]{"twogoods", 3}, new TestServiceIfaceImpl());
        System.out.println(breaker.execute(task));
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000l);
            breaker.execute(task);
        }
    }
}