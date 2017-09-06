package com.tg.rpc.core.proxy;


/**
 * Created by twogoods on 16/10/24.
 */
public interface ClientProxy {
    <T> T getProxy(Class<T> serviceInterface);
}
