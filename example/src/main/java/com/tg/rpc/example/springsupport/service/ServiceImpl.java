package com.tg.rpc.example.springsupport.service;

import com.tg.rpc.springsupport.annotation.RpcService;

/**
 * Created by twogoods on 17/2/18.
 */
@RpcService()
public class ServiceImpl {

    public String test() {
        return "haha";
    }
}
