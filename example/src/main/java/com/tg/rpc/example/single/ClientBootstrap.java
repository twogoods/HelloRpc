package com.tg.rpc.example.single;

import com.tg.rpc.core.config.ClientProperty;
import com.tg.rpc.core.proxy.DefaultClientInterceptor;
import com.tg.rpc.example.service.TestService;
import com.tg.rpc.core.bootstrap.Client;

import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.core.proxy.JdkClientProxy;
import com.tg.rpc.example.service.EchoService;

/**
 * Created by twogoods on 17/2/17.
 */
public class ClientBootstrap {
    public static void main(String[] args) throws Exception {
        ClientProperty clientA = new ClientProperty();
        clientA.serviceName("A")
                .provider("127.0.0.1:9001")
                .interfaces("com.tg.rpc.example.service.EchoService");
        ClientProperty clientB = new ClientProperty();
        clientB.serviceName("B")
                .provider("127.0.0.1:9001")
                .interfaces("com.tg.rpc.example.service.TestService");
        Client client = new Client.Builder().host("127.0.0.1")
                .port(9001)
                .maxCapacity(3)
                .requestTimeoutMillis(3500)
                .connectionMaxTotal(10)
                .connectionMaxIdle(6)
                .client(clientA)
                .client(clientB)
                .build();
        DefaultClientInterceptor interceptor = new DefaultClientInterceptor(client);
        ClientProxy clientProxy = new JdkClientProxy(interceptor);

        EchoService echoService = clientProxy.getProxy(EchoService.class);
        System.out.println(echoService.echo("twogoods"));

        TestService testService = clientProxy.getProxy(TestService.class);
        System.out.println(testService.add(2, 5));
    }
}
