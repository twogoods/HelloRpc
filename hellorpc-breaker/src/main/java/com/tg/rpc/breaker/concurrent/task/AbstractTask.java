package com.tg.rpc.breaker.concurrent.task;

import com.tg.rpc.breaker.fallback.Fallback;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/26.
 */
public abstract class AbstractTask implements Task {
    protected Method metricsMethod;
    protected Object[] args;
    protected Fallback fallback;
    protected long timeoutInMillis = 5000l;

    public AbstractTask(Method metricsMethod, Object[] args) {
        this.metricsMethod = metricsMethod;
        this.args = args;
        fallback = new Fallback(metricsMethod);
    }

    public AbstractTask(Method metricsMethod, Object[] args, long timeoutInMillis) {
        this.metricsMethod = metricsMethod;
        this.args = args;
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

    public Object[] getCallArgs() {
        return args;
    }

    @Override
    public long getTimeoutInMillis() {
        return timeoutInMillis;
    }


}
