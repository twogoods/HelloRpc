package com.tg.rpc.core.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by twogoods on 17/2/17.
 */
public class TestInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(Method method, Object[] args, Class clazz) throws Throwable {
        System.out.println("method : " + method);
        System.out.println("class : " + clazz);
        System.out.println("args : " + Arrays.asList(args));
        return "test";
    }
}
