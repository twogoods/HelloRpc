package com.tg.rpc.core.util;

import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.core.proxy.DefaultClientInterceptor;
import com.tg.rpc.core.proxy.JdkClientProxy;

/**
 * @author twogoods
 * @since 2017/7/10
 */
public class ClientUtil {


    public <T> T getClient(Client client, Class<T> clazz) {
        DefaultClientInterceptor interceptor = new DefaultClientInterceptor(client);
        ClientProxy clientProxy = new JdkClientProxy(interceptor);
        return clientProxy.getProxy(clazz);
    }

}
