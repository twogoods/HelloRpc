package com.tg.rpc.example.single;

import com.tg.rpc.core.bootstrap.Server;
import com.tg.rpc.core.exception.ValidateException;
import com.tg.rpc.core.handler.response.ResponseHandler;
import com.tg.rpc.example.server.EchoService;
import com.tg.rpc.example.server.EchoServiceImpl;
import com.tg.rpc.example.server.TestService;
import com.tg.rpc.example.server.TestServiceImpl;

/**
 * Created by twogoods on 17/2/17.
 */
public class ServerBootstrap {
    public static void main(String[] args) throws ValidateException {
        //端口号,最大的传输容量(单位M),服务响应的Handle
        Server server = new Server.Builder().port(9001).maxCapacity(3).build();
        server.addService(EchoService.class, new EchoServiceImpl())
                .addService(TestService.class,new TestServiceImpl());
        server.start();
    }
}
