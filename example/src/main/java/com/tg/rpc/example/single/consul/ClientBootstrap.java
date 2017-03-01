package com.tg.rpc.example.single.consul;

import com.tg.rpc.consul.ConsulCompentFactory;
import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.core.proxy.DefaultClientInterceptor;
import com.tg.rpc.core.proxy.JdkClientProxy;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
import com.tg.rpc.example.service.EchoService;
import com.tg.rpc.example.service.TestService;

/**
 * Created by twogoods on 17/2/17.
 */
public class ClientBootstrap {
    public static void main(String[] args) throws Exception {
        ServiceDiscovery serviceDiscovery = ConsulCompentFactory.getDiscovery();
        Client client = new Client.Builder().serviceDiscovery(serviceDiscovery).maxCapacity(3).build();
        DefaultClientInterceptor interceptor = new DefaultClientInterceptor(client);
        ClientProxy clientProxy = new JdkClientProxy(interceptor);

        EchoService echoService = clientProxy.getProxy(EchoService.class);
        System.out.println(echoService.echo("twogoods"));

        TestService testService = clientProxy.getProxy(TestService.class);
        System.out.println(testService.add(2, 5));

    }
}
