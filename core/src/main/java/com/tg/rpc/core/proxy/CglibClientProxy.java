package com.tg.rpc.core.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 16/10/24.
 */
public class CglibClientProxy extends AbstractClientProxy {
    protected Enhancer enhancer = new Enhancer();

    public CglibClientProxy(com.tg.rpc.core.proxy.MethodInterceptor interceptor) {
        super(interceptor);
    }

    @Override
    public <T> T getProxy(Class<T> serviceInterface) {
        return getProxy(serviceInterface,null);
    }

    @Override
    public <T> T getProxy(final Class<T> serviceInterface, final String serviceName) {
        enhancer.setSuperclass(serviceInterface);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                return interceptor.invoke(method, args, serviceInterface, serviceName);
            }
        });
        return (T) enhancer.create();
    }
}
