package com.tg.rpc.example.single.consul;

import com.tg.rpc.consul.ConsulCompentFactory;
import com.tg.rpc.consul.ConsulRegistry;
import com.tg.rpc.core.bootstrap.Server;
import com.tg.rpc.core.exception.ValidateException;
import com.tg.rpc.core.servicecenter.ServiceRegistry;
import com.tg.rpc.example.service.EchoService;
import com.tg.rpc.example.service.EchoServiceImpl;
import com.tg.rpc.example.service.TestService;
import com.tg.rpc.example.service.TestServiceImpl;

/**
 * Created by twogoods on 17/2/17.
 */
public class ServerBootstrap {
    public static void main(String[] args) throws ValidateException {
        ServiceRegistry serviceRegistry= ConsulCompentFactory.getRegistry();
        Server server = new Server.Builder().port(9001).maxCapacity(3).serviceRegistry(serviceRegistry).build();
        server.addService(EchoService.class, new EchoServiceImpl())
                .addService(TestService.class,new TestServiceImpl());
        server.start();
    }
}
