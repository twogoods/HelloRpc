package com.tg.rpc.breaker.concurrent.task;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2017/7/27.
 */
public class HookTask<T, K> extends AbstractTask {
    private TaskExecuteHook taskExecuteHook;
    private T t;

    public HookTask(TaskExecuteHook taskExecuteHook, T t, ArgsHook argsHook, Method metricsMethod) {
        super(metricsMethod, argsHook.getCallArgs());
        this.taskExecuteHook = taskExecuteHook;
        this.t = t;
    }

    public HookTask(TaskExecuteHook taskExecuteHook, T t, ArgsHook argsHook, Method metricsMethod, long timeoutInMillis) {
        super(metricsMethod, argsHook.getCallArgs(), timeoutInMillis);
        this.taskExecuteHook = taskExecuteHook;
        this.t = t;
    }

    @Override
    public Object call() throws Exception {
        return taskExecuteHook.execute(t);
    }
}
