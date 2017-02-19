package com.tg.rpc.core.handler.response;

import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseCodeConstant;
import com.tg.rpc.core.entity.ServiceHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 17/2/17.
 */
public class DefaultResponseHandler implements ResponseHandler {

    private static Logger log = LogManager.getLogger(DefaultResponseHandler.class);

    @Override
    public Response handle(Request request) {
        Object serviceimpl = ServiceHolder.get(request);
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        if (serviceimpl != null) {
            try {
                Method method = serviceimpl.getClass().getMethod(request.getMethod(), request.getParameterTypes());
                response.setReturnObj(method.invoke(serviceimpl, request.getParams()));
            } catch (Exception e) {
                log.error("server method invoke error! request:{}",request);
                response.setCode(ResponseCodeConstant.INTERNAL_ERROR);
            }
        } else {
            response.setCode(ResponseCodeConstant.SERVICE_NOT_FIND);
        }
        return response;
    }
}
