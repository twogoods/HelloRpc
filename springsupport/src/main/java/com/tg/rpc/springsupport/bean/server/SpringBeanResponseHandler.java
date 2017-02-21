package com.tg.rpc.springsupport.bean.server;

import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.entity.ResponseCodeConstant;
import com.tg.rpc.core.handler.response.ResponseHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Description:
 *   server端请求处理
 * @author twogoods
 * @version 0.1
 * @since 2017-02-18
 */
@Component
public class SpringBeanResponseHandler implements ResponseHandler, BeanFactoryAware {

    private static Logger log = LogManager.getLogger(SpringBeanResponseHandler.class);

    private BeanFactory beanFactory;

    @Override
    public Response handle(Request request) {
        String serviceName = request.getServiceName();
        Object serviceImpl;
        if (!StringUtils.isEmpty(serviceName)) {
            serviceImpl = beanFactory.getBean(serviceName);
        } else {
            //TODO 没有servicename时,通过接口使用类似Spring bytype 的形式拿到bean;
            serviceImpl = beanFactory.getBean(serviceName);
        }
        Response response = new Response();
        try {
            Method method = serviceImpl.getClass().getMethod(request.getMethod(), request.getParameterTypes());
            Object ret = method.invoke(serviceImpl, request.getParams());
            response.setRequestId(request.getRequestId());
            response.setReturnObj(ret);
            response.setCode(ResponseCodeConstant.SUCCESS);
        } catch (Exception e) {
            log.error("server method invoke error! request:{}", request);
            response.setCode(ResponseCodeConstant.INTERNAL_ERROR);
        }
        return response;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
