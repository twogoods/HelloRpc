package com.tg.rpc.core.proxy;

import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseCodeConstant;
import com.tg.rpc.core.exception.ServiceInvokeException;
import com.tg.rpc.core.exception.ServiceInvokeTimeOutException;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 17/2/17.
 */
public class DefaultClientInterceptor implements MethodInterceptor {

    private Client client;

    public DefaultClientInterceptor() {
    }

    public DefaultClientInterceptor(Client client) {
        this.client = client;
    }

    @Override
    public Object invoke(Method method, Object[] args, Class clazz) throws Throwable {
        //TODO client发起的调用，限制调用频率，熔断
        //breaker的配置
        Response response = client.sendRequest(method, args, clazz);
        if(response==null){
            throw new ServiceInvokeTimeOutException("response timout");
        }
        if(response.getCode()== ResponseCodeConstant.SERVICE_NOT_FIND){
            throw new ServiceInvokeException("server can not find ServiceImpl, please check you implements service");
        }else if(response.getCode()== ResponseCodeConstant.INTERNAL_ERROR){
            throw new ServiceInvokeException("server invoke error, please check server's logs");
        }
        return response.getReturnObj();
    }
}
