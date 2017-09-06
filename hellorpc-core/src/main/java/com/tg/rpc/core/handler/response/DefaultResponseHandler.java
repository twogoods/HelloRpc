package com.tg.rpc.core.handler.response;

import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseStatus;
import com.tg.rpc.core.entity.ServiceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 17/2/17.
 */
public class DefaultResponseHandler implements ResponseHandler {

    private static Logger log = LoggerFactory.getLogger(DefaultResponseHandler.class);

    @Override
    public Response handle(Request request) {
        Object serviceimpl = ServiceHolder.get(request);
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        if (serviceimpl == null) {
            response.setStatus(ResponseStatus.SERVICE_NOT_FIND);
            return response;
        }
        try {
            // 反射做缓存
            Method method = serviceimpl.getClass().getMethod(request.getMethod(), request.getParameterTypes());
            response.setReturnObj(method.invoke(serviceimpl, request.getParams()));
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("server method invoke error! request:{}", request);
            response.setStatus(ResponseStatus.INTERNAL_ERROR);
        }
        return response;
    }
}
