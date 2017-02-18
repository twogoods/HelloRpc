package com.tg.rpc.springsupport.bean.server;

import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseCodeConstant;
import com.tg.rpc.core.handler.response.ResponseHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 17/2/18.
 */
@Component
public class SpringBeanResponseHandler implements ResponseHandler {

    private static Logger log = LogManager.getLogger(SpringBeanResponseHandler.class);
    @Autowired
    private SpringContextHolder springContextHolder;

    @Override
    public Response handle(Request request) {
        String serviceName = request.getServiceName();
        Object serviceImpl;
        if (!StringUtils.isEmpty(serviceName)) {
            serviceImpl = springContextHolder.getBean(serviceName);
        } else {
            serviceImpl = springContextHolder.getBean(serviceName);
        }
        Response response = new Response();
        try {
            Method method = serviceImpl.getClass().getMethod(request.getMethod(), request.getParameterTypes());
            Object ret = method.invoke(serviceImpl, request.getParams());
            response.setRequestId(request.getRequestId());
            response.setReturnObj(ret);
            response.setCode(ResponseCodeConstant.SUCCESS);
        } catch (Exception e) {
            log.error("server method invoke error! request:{}",request);
            response.setCode(ResponseCodeConstant.INTERNAL_ERROR);
        }
        return response;
    }
}
