package com.tg.rpc.breaker.concurrent.task;

import com.tg.rpc.breaker.exception.BreakerException;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/25.
 */
public class RpcTask extends ReflectTask {
    private Method warpMethod;

    public RpcTask(Method metricsMethod, Object[] args, Object obj, Method warpMethod) {
        super(metricsMethod, args, obj);
        this.warpMethod = warpMethod;
    }

    public RpcTask(Method metricsMethod, Object[] args, Object obj, long timeoutInMillis, Method warpMethod) {
        super(metricsMethod, args, obj, timeoutInMillis);
        this.warpMethod = warpMethod;
    }

    @Override
    public Object call() throws Exception {
        if (obj == null) {
            throw new BreakerException("class instance is null");
        }
        return warpMethod.invoke(obj, args);
    }
}
