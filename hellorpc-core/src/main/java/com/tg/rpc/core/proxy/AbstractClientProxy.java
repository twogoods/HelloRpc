package com.tg.rpc.core.proxy;


/**
 * Created by twogoods on 16/10/24.
 */
public abstract class AbstractClientProxy implements ClientProxy {

    protected MethodInterceptor interceptor;

    public AbstractClientProxy(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

}
