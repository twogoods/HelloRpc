package com.tg.rpc.breaker.concurrent;

import com.tg.rpc.breaker.Task;

/**
 * Created by twogoods on 2017/7/23.
 */
public interface Executor {
    Object execute(Task task) throws Throwable;
}
