package com.tg.rpc.breaker.concurrent.task;


import com.tg.rpc.breaker.Exception.BreakerException;
import com.tg.rpc.breaker.Fallback.Fallback;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/25.
 */
public class RpcTask implements Task {

    private Method warpMethod;
    private Object[] args;
    private Object obj;
    private Method metricsMethod;
    private Fallback fallback;
    private long timeoutInMillis = 3000l;

    public RpcTask(Method warpMethod, Object[] args, Object obj, Method metricsMethod) {
        this.warpMethod = warpMethod;
        this.args = args;
        this.obj = obj;
        this.metricsMethod = metricsMethod;
        fallback = new Fallback(metricsMethod);
    }

    public RpcTask(Method warpMethod, Object[] args, Object obj, Method metricsMethod, long timeoutInMillis) {
        this.warpMethod = warpMethod;
        this.args = args;
        this.obj = obj;
        this.metricsMethod = metricsMethod;
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
        return warpMethod.invoke(obj, args);
    }
}
