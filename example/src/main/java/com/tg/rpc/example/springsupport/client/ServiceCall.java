package com.tg.rpc.example.springsupport.client;

import com.tg.rpc.example.service.EchoService;
import com.tg.rpc.example.service.TestService;
import com.tg.rpc.springsupport.annotation.RpcReferer;
import org.springframework.stereotype.Component;

/**
 * Created by twogoods on 17/2/19.
 */
@Component
public class ServiceCall {

    @RpcReferer
    private EchoService echoService;

    @RpcReferer
    private TestService testService;


    public String echo(String s) {
        return echoService.echo(s);
    }

    public int add(int a, int b) {
        return testService.add(a, b);
    }
}
