package com.tg.rpc.breaker.concurrent.task;

import com.tg.rpc.breaker.exception.BreakerException;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/23.
 */
public class ReflectTask extends AbstractTask {
    protected Object obj;

    public ReflectTask(Method metricsMethod, Object[] args, Object obj) {
        super(metricsMethod, args);
        this.obj = obj;
    }

    public ReflectTask(Method metricsMethod, Object[] args, Object obj, long timeoutInMillis) {
        super(metricsMethod, args, timeoutInMillis);
        this.obj = obj;
    }

    @Override
    public Object call() throws Exception {
        if (obj == null) {
            throw new BreakerException("class instance is null");
        }
        return metricsMethod.invoke(obj, args);
    }
}