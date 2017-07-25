package com.tg.rpc.breaker.concurrent.task;

import com.tg.rpc.breaker.Exception.BreakerException;
import com.tg.rpc.breaker.Fallback.Fallback;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/23.
 */
public class CommonTask implements Task {
    private Method metricsMethod;
    private Object[] args;
    private Object obj;
    private Fallback fallback;
    private long timeoutInMillis = 3000l;

    public CommonTask(Method metricsMethod, Object[] args, Object obj) {
        this.metricsMethod = metricsMethod;
        this.args = args;
        this.obj = obj;
        fallback = new Fallback(metricsMethod);
    }

    public CommonTask(Method metricsMethod, Object[] args, Object obj, long timeoutInMillis) {
        this.metricsMethod = metricsMethod;
        this.args = args;
        this.obj = obj;
        this.timeoutInMillis = timeoutInMillis;
        fallback = new Fallback(metricsMethod);
    }


    @Override
    public boolean supportFallback() {
        return fallback.supportFallback();
    }

    @Override
    public Object callFallback() throws Throwable {
        return fallback.callFallback(args);
    }

    @Override
    public Method getMetricsMethod() {
        return metricsMethod;
    }

    @Override
    public Object[] getCallArgs() {
        return args;
    }

    @Override
    public long getTimeoutInMillis() {
        return timeoutInMillis;
    }

    @Override
    public Object call() throws Exception {
        if (obj == null) {
            throw new BreakerException("class instance is null");
        }
        return metricsMethod.invoke(obj, args);
    }
}
