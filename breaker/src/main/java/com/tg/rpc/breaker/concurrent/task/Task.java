package com.tg.rpc.breaker.concurrent.task;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by twogoods on 2017/7/25.
 */
public interface Task extends Callable<Object> {
    boolean supportFallback();

    Object callFallback() throws Throwable;

    Method getMetricsMethod();

    default long getTimeoutInMillis() {
        return 3000l;
    }
}
