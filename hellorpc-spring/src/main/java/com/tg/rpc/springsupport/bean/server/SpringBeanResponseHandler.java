package com.tg.rpc.springsupport.bean.server;

import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseStatus;
import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.handler.response.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Description:
 * server端请求处理
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-18
 */
@Component
public class SpringBeanResponseHandler implements ResponseHandler, BeanFactoryAware {

    private static Logger log = LoggerFactory.getLogger(SpringBeanResponseHandler.class);

    private BeanFactory beanFactory;

    @Override
    public Response handle(Request request) {
        Object serviceImplObj = beanFactory.getBean(request.getClazz());
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        if (serviceImplObj == null) {
            response.setStatus(ResponseStatus.SERVICE_NOT_FIND);
            return response;
        }
        try {
            Method method = serviceImplObj.getClass().getMethod(request.getMethod(), request.getParameterTypes());
            Object ret = method.invoke(serviceImplObj, request.getParams());
            response.setReturnObj(ret);
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("server method invoke error! request:{}", request);
            response.setStatus(ResponseStatus.INTERNAL_ERROR);
        }
        return response;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
