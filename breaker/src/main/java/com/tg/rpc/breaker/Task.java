package com.tg.rpc.breaker;

import com.tg.rpc.breaker.Fallback.Fallback;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by twogoods on 2017/7/23.
 */
@Data
public class Task implements Callable<Object> {
    private Method method;
    private Object[] args;
    private Fallback fallback;
    private long timeoutInMillis = 2000l;

    public Task(Method method, Object[] args) {
        this.method = method;
        this.args = args;
        fallback = new Fallback(method);
    }

    public boolean supportFallback() {
        return fallback.supportFallback();
    }

    public Object callFallback() throws Throwable {
        return fallback.callFallback(args);
    }

    @Override
    public Object call() throws Exception {
        return null;
    }
}
