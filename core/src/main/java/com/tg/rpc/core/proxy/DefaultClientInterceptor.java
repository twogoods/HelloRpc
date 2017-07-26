package com.tg.rpc.core.proxy;

import com.tg.rpc.breaker.Exception.BreakerException;
import com.tg.rpc.breaker.concurrent.task.RpcTask;
import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseStatus;
import com.tg.rpc.core.exception.ServiceInvokeException;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 17/2/17.
 */
public class DefaultClientInterceptor implements MethodInterceptor {

    private Client client;
    private Method sendRequestMethod;

    public DefaultClientInterceptor() {
    }

    public DefaultClientInterceptor(Client client) {
        this.client = client;
        if (client.getBreaker() != null) {
            try {
                sendRequestMethod = Client.class.getMethod("sendRequest", Request.class);
            } catch (NoSuchMethodException e) {
                throw new BreakerException(e);
            }
        }
    }

    @Override
    public Object invoke(Method method, Object[] args) throws Throwable {
        Request request = new Request(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), args);
        Response response;
        if (client.getBreaker() != null) {
            Object callResult = client.getBreaker().execute(new RpcTask(method, new Object[]{request}, client, sendRequestMethod));
            response = (Response) callResult;
        } else {
            response = client.sendRequest(request);
        }
        if (response.getCode() != ResponseStatus.SUCCESS.getCode()) {
            throw new ServiceInvokeException(String.format("server return code:%d ,msg:%s", response.getCode(), response.getMsg()));
        }
        return response.getReturnObj();
    }
}
