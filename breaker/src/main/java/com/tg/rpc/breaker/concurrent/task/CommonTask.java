package com.tg.rpc.breaker.concurrent.task;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/23.
 */
public class CommonTask extends AbstractTask {
    public CommonTask(Method metricsMethod, Object[] args, Object obj) {
        super(metricsMethod, args, obj);
    }

    public CommonTask(Method metricsMethod, Object[] args, Object obj, long timeoutInMillis) {
        super(metricsMethod, args, obj, timeoutInMillis);
    }
}
