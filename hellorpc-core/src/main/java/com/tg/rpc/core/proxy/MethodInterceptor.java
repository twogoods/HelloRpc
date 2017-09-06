package com.tg.rpc.core.proxy;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 16/10/24.
 */
public interface MethodInterceptor {

    Object invoke(Method method, Object[] args) throws Throwable;

}