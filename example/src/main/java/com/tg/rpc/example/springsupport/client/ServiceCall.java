package com.tg.rpc.example.springsupport.client;

import com.tg.rpc.example.springsupport.service.Service;
import com.tg.rpc.springsupport.annotation.RpcReferer;
import org.springframework.stereotype.Component;

/**
 * Created by twogoods on 17/2/19.
 */
@Component
public class ServiceCall {
    @RpcReferer(name = "serviceImpl")
    private Service service;

    public String call(){
        return service.test();
    }
}
