package com.tg.rpc.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by twogoods on 16/10/24.
 */
public class JdkClientProxy extends AbstractClientProxy {

    public JdkClientProxy(MethodInterceptor interceptor) {
        super(interceptor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(final Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return interceptor.invoke(method, args);
            }
        });
    }

}
