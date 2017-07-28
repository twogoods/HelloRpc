package com.tg.rpc.core.proxy;

import com.tg.rpc.breaker.concurrent.task.HookTask;
import com.tg.rpc.breaker.concurrent.task.TaskExecuteHook;
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
    private TaskExecuteHook<Request, Response> taskExecuteHook;

    public DefaultClientInterceptor(Client client) {
        this.client = client;
        taskExecuteHook = client::sendRequest;
    }

    @Override
    public Object invoke(Method method, Object[] args) throws Throwable {
        Request request = new Request(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), args);
        Response response;
        if (client.getBreaker() != null) {
            Object callResult = client.getBreaker().execute(new HookTask<>(taskExecuteHook, request, request::getParams, method));
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
