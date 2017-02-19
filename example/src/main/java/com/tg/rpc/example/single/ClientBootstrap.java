package com.tg.rpc.example.single;

import com.tg.rpc.core.bootstrap.Client;

import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.core.proxy.DefaultClientInterceptor;
import com.tg.rpc.core.proxy.JdkClientProxy;
import com.tg.rpc.example.server.EchoService;
import com.tg.rpc.example.server.TestService;

/**
 * Created by twogoods on 17/2/17.
 */
public class ClientBootstrap {
    public static void main(String[] args) throws Exception {
        Client client = new Client.Builder().host("127.0.0.1").port(9001).maxCapacity(3).build();
        DefaultClientInterceptor interceptor = new DefaultClientInterceptor(client);
        ClientProxy clientProxy = new JdkClientProxy(interceptor);

        EchoService echoService = clientProxy.getProxy(EchoService.class);
        System.out.println(echoService.hello("twogoods"));

        TestService testService = clientProxy.getProxy(TestService.class);
        System.out.println(testService.add(2, 5));

    }
}
