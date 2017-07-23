package com.tg.rpc.core.proxy;

import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseStatus;
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
    public Object invoke(Method method, Object[] args) throws Throwable {
        //TODO client发起的调用，限制调用频率，熔断
        //breaker的配置


        Response response = client.sendRequest(method, args);
        if (response == null) {
            throw new ServiceInvokeTimeOutException("response timout");
        }
        if (response.getCode() != ResponseStatus.SUCCESS.getCode()) {
            throw new ServiceInvokeException(String.format("server return code:%d ,msg:%s", response.getCode(), response.getMsg()));
        }
        return response.getReturnObj();
    }
}
