package com.tg.rpc.breaker.concurrent.task;

import com.tg.rpc.breaker.Exception.BreakerException;
import com.tg.rpc.breaker.Fallback.Fallback;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/26.
 */
public abstract class AbstractTask implements Task {
    protected Method metricsMethod;
    protected Object[] args;
    protected Object obj;
    protected Fallback fallback;
    protected long timeoutInMillis = 3000l;

    public AbstractTask(Method metricsMethod, Object[] args, Object obj) {
        this.metricsMethod = metricsMethod;
        this.args = args;
        this.obj = obj;
    }


    public AbstractTask(Method metricsMethod, Object[] args, Object obj, long timeoutInMillis) {
        this.metricsMethod = metricsMethod;
        this.args = args;
        this.obj = obj;
        this.timeoutInMillis = timeoutInMillis;
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
