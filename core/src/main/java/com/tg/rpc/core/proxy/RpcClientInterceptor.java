package com.tg.rpc.core.proxy;

import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseCodeConstant;
import com.tg.rpc.core.exception.ServiceInvokeException;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 17/2/17.
 */
public class RpcClientInterceptor implements MethodInterceptor {

    private Client client;

    public RpcClientInterceptor() {
    }

    public RpcClientInterceptor(Client client) {
        this.client = client;
    }

    @Override
    public Object invoke(Method method, Object[] args, Class clazz, String serviceName) throws Throwable {
        Response response = client.sendRequest(method, args, clazz,serviceName);
        if(response==null){
            throw new ServiceInvokeException("server didn't return response");
        }
        if(response.getCode()== ResponseCodeConstant.SERVICE_NOT_FIND){
            throw new ServiceInvokeException("server can not find ServiceImpl, please check you implements service");
        }else if(response.getCode()== ResponseCodeConstant.INTERNAL_ERROR){
            throw new ServiceInvokeException("server invoke error, please check server's logs");
        }
        return response.getReturnObj();
    }
}
